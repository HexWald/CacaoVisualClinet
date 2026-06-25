package net.cacaovisualclient.mod.integration.discord;

import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.entities.RichPresence;
import com.jagrosh.discordipc.entities.pipe.PipeStatus;
import com.jagrosh.discordipc.exceptions.NoDiscordClientException;
import net.cacaovisualclient.mod.CacaoVisualClient;

import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public final class DiscordRichPresenceService implements AutoCloseable {

    private static final long UPDATE_INTERVAL_SECONDS = 5;

    private final AtomicReference<PresenceData> latestPresence = new AtomicReference<>();
    private final long startedAt = Instant.now().getEpochSecond();

    private ScheduledExecutorService executor;
    private IPCClient client;
    private PresenceData lastSentPresence;
    private boolean connectionFailureLogged;

    public synchronized boolean start(String applicationId) {
        if (executor != null) {
            return true;
        }

        final long clientId;
        try {
            clientId = Long.parseLong(applicationId);
        } catch (NumberFormatException e) {
            CacaoVisualClient.LOGGER.warn("Discord Application ID must be a numeric value");
            return false;
        }

        final ThreadFactory threadFactory = task -> {
            final Thread thread = new Thread(task, "CacaoVisualClient Discord RPC");
            thread.setDaemon(true);
            return thread;
        };

        client = new IPCClient(clientId);
        executor = Executors.newSingleThreadScheduledExecutor(threadFactory);
        executor.scheduleWithFixedDelay(
                this::updateDiscord,
                0,
                UPDATE_INTERVAL_SECONDS,
                TimeUnit.SECONDS
        );
        return true;
    }

    public void update(String details, String state) {
        latestPresence.set(new PresenceData(details, state));
    }

    private void updateDiscord() {
        try {
            ensureConnected();

            final PresenceData presence = latestPresence.get();
            if (presence == null || presence.equals(lastSentPresence)) {
                return;
            }

            client.sendRichPresence(new RichPresence.Builder()
                    .setDetails(presence.details())
                    .setState(presence.state())
                    .setStartTimestamp(startedAt)
                    .build());
            lastSentPresence = presence;
        } catch (Exception e) {
            if (!connectionFailureLogged) {
                CacaoVisualClient.LOGGER.info("Discord Rich Presence is waiting for the Discord desktop client");
                connectionFailureLogged = true;
            }
        }
    }

    private void ensureConnected() throws NoDiscordClientException {
        if (client.getStatus() == PipeStatus.CONNECTED) {
            return;
        }

        client.connect();
        connectionFailureLogged = false;
        lastSentPresence = null;
        CacaoVisualClient.LOGGER.info("Discord Rich Presence connected");
    }

    @Override
    public synchronized void close() {
        if (executor == null) {
            return;
        }

        executor.shutdownNow();
        executor = null;
        latestPresence.set(null);
        lastSentPresence = null;

        if (client != null && client.getStatus() == PipeStatus.CONNECTED) {
            try {
                client.sendRichPresence(null);
                client.close();
            } catch (Exception e) {
                CacaoVisualClient.LOGGER.debug("Failed to close Discord Rich Presence cleanly", e);
            }
        }

        client = null;
        connectionFailureLogged = false;
    }

    private record PresenceData(String details, String state) {

        private PresenceData {
            Objects.requireNonNull(details, "details");
            Objects.requireNonNull(state, "state");
        }
    }
}
