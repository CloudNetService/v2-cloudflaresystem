package eu.cloudnetservice.cloudnet.v2.modules.cloudflare.module;

import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import eu.cloudnetservice.cloudnet.v2.master.module.JavaCloudModule;
import eu.cloudnetservice.cloudnet.v2.modules.cloudflare.module.config.ConfigCloudFlare;
import eu.cloudnetservice.cloudnet.v2.modules.cloudflare.module.listener.ProxyAddListener;
import eu.cloudnetservice.cloudnet.v2.modules.cloudflare.module.listener.ProxyRemoveListener;
import eu.cloudnetservice.cloudnet.v2.modules.cloudflare.module.services.CloudFlareService;

/**
 * This is the main class of the CloudFlare module for CloudNet
 *
 * Its purpose is to register necessary event handlers in CloudNet and start the verification process of the
 * CloudFlare token in the configurations.
 */
public final class CloudflareModule extends JavaCloudModule {

    private CloudFlareService cloudFlareService;

    public CloudFlareService getCloudFlareService() {
        return cloudFlareService;
    }

    @Override
    public void onEnable() {
        final ConfigCloudFlare config = new ConfigCloudFlare();
        this.cloudFlareService = new CloudFlareService(config.load(), this);
        NetworkUtils.getExecutor().execute(this.cloudFlareService::bootstrap);

        this.getCloud().getEventManager().registerListener(this, new ProxyAddListener(this.cloudFlareService));
        this.getCloud().getEventManager().registerListener(this, new ProxyRemoveListener(this.cloudFlareService));
    }

    @Override
    public void onDisable() {
        try {
            this.cloudFlareService.shutdown();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
