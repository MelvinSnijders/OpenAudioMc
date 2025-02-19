package com.craftmend.openaudiomc.generic.commands.subcommands;

import com.craftmend.openaudiomc.OpenAudioMc;
import com.craftmend.openaudiomc.api.impl.event.events.SystemReloadEvent;
import com.craftmend.openaudiomc.api.interfaces.AudioApi;
import com.craftmend.openaudiomc.generic.commands.interfaces.SubCommand;
import com.craftmend.openaudiomc.generic.commands.objects.Argument;
import com.craftmend.openaudiomc.generic.craftmend.CraftmendService;
import com.craftmend.openaudiomc.generic.client.objects.ClientConnection;
import com.craftmend.openaudiomc.generic.networking.interfaces.NetworkingService;
import com.craftmend.openaudiomc.generic.platform.Platform;
import com.craftmend.openaudiomc.generic.user.User;

public class ReloadSubCommand extends SubCommand {

    public ReloadSubCommand() {
        super("reload", "restart");
        registerArguments(new Argument("", "Reloads the config.yml and connection system"));
    }

    @Override
    public void onExecute(User sender, String[] args) {
        message(sender, Platform.makeColor("RED") + "Reloading OpenAudioMc data (config and account details)...");
        OpenAudioMc.getInstance().getConfiguration().reloadConfig();
        OpenAudioMc.getService(CraftmendService.class).syncAccount();

        message(sender, Platform.makeColor("RED") + "Shutting down network service and logging out...");

        for (ClientConnection client : OpenAudioMc.getService(NetworkingService.class).getClients()) {
            client.kick(() -> {});
        }

        OpenAudioMc.getService(NetworkingService.class).stop();

        message(sender, Platform.makeColor("RED") + "Re-activating account...");
        OpenAudioMc.getService(NetworkingService.class).connectIfDown();

        AudioApi.getInstance().getEventDriver().fire(new SystemReloadEvent());

        message(sender, Platform.makeColor("GREEN") + "Reloaded system! Welcome back.");
    }
}
