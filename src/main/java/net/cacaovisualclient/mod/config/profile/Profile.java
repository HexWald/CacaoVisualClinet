package net.cacaovisualclient.mod.config.profile;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.cacaovisualclient.mod.module.Module;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class Profile {

    private final String name;
    private final List<Module> enabledModules;

}
