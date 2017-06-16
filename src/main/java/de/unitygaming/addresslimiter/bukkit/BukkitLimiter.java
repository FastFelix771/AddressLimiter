package de.unitygaming.addresslimiter.bukkit;

import static java.lang.String.format;

import java.io.File;
import java.net.InetAddress;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import de.unitygaming.addresslimiter.AddressLimiter;
import de.unitygaming.addresslimiter.Config;

public final class BukkitLimiter extends JavaPlugin implements Listener {

	private static AddressLimiter limiter;
	private static Config config;


	@Override
	public void onEnable() {
		setupConfig();
		limiter = new AddressLimiter(config.getLimit());

		getServer().getPluginManager().registerEvents(this, this);
	}

	@Override
	public void onDisable() {
		limiter = null;
		config = null;
	}

	private void setupConfig() {
		File file = new File(getDataFolder(), "config.xml");
		file.getParentFile().mkdirs();

		try {
			if (!file.exists()) {
				config = new Config();

				Marshaller marshaller = JAXBContext.newInstance(Config.class).createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

				marshaller.marshal(config, file);
				return;
			}

			config = JAXB.unmarshal(file, Config.class);
		} catch (JAXBException e) {
			getLogger().warning("Failed to save / load configuration file!");
			e.printStackTrace();

			config = new Config();
		} finally {
			if (config.getLimit() < 1) {
				getLogger().warning(format("Invalid address limit set! (%d) - falling back to default value. (3).", config.getLimit()));
				config.setLimit(3);
				return;
			}

			getLogger().info(format("Concurrent users per IP address allowed: %d", config.getLimit()));
		}
	}

	@EventHandler
	public void onPreLogin(AsyncPlayerPreLoginEvent event) {
		InetAddress address = event.getAddress();

		if (!limiter.check(address)) {
			event.disallow(Result.KICK_OTHER, config.getCancelReason());
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		InetAddress address = event.getPlayer().getAddress().getAddress();
		limiter.increment(address);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		InetAddress address = event.getPlayer().getAddress().getAddress();
		limiter.decrement(address);
	}

}
