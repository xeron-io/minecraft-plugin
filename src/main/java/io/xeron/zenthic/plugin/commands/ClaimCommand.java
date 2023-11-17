package io.xeron.zenthic.plugin.commands;

import com.google.gson.Gson;
import io.xeron.zenthic.plugin.Plugin;
import io.xeron.zenthic.plugin.models.ResponseModel;
import io.xeron.zenthic.plugin.utils.GetSlots;
import io.xeron.zenthic.plugin.utils.Hmac;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.io.IOException;

public class ClaimCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            Plugin plugin = Plugin.getPlugin(Plugin.class);
            String privateKey = plugin.getCustomConfig().getString("privateKey");
            String apiKey = plugin.getCustomConfig().getString("apiKey");

            if (args.length < 1) {
                sender.sendMessage("\u00A7c" + "Usage: /claim [reference]");
                return false;
            }

            if (privateKey == null || apiKey == null) {
                sender.sendMessage("You must set your private key and API key in the config.yml file.");
                return false;
            }

            String transactionId = args[0];
            String signature = Hmac.calcHmacSha256(privateKey, transactionId);

            // get how many empty slots the player has
            int currentEmptySlots = GetSlots.getEmptySlots((Player) sender);

            OkHttpClient client = new OkHttpClient();
            FormBody body = new FormBody.Builder()
                    .add("apiKey", apiKey)
                    .add("transactionId", transactionId)
                    .add("signature", signature)
                    .add("minSlot", String.valueOf(currentEmptySlots))
                    .build();

            Request request = new Request.Builder()
                    .url("https://api.xeron.io/callback")
                    .post(body)
                    .build();

            try {
                Gson gson = new Gson();
                Response response = client.newCall(request).execute();
                ResponseModel responseModel = gson.fromJson(response.body().string(), ResponseModel.class);

                if (responseModel.code.equals("200")) {
                    JSONObject data = responseModel.data;
                    String commandToRun = data.get("command").toString().replace("{username}", sender.getName());
                    int quantity = Integer.parseInt(data.get("quantity").toString());

                    sender.sendMessage("\u00A7a" + responseModel.message);
                    for (int i = 0; i < quantity; i++) {
                        sender.getServer().dispatchCommand(sender.getServer().getConsoleSender(), commandToRun);
                    }
                } else {
                    sender.sendMessage("\u00A7c" + responseModel.message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            sender.sendMessage("You must be a player!");
            return false;
        }
        return false;
    }
}
