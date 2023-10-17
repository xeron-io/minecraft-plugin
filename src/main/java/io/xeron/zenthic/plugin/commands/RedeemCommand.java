package io.xeron.zenthic.plugin.commands;

import com.google.gson.Gson;
import io.xeron.zenthic.plugin.models.ResponseModel;
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

public class RedeemCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            String privateKey = "4a2920fe-7aa3-43be-9c47-5c1fe8e3826f";
            String apiKey = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJ4ZXJvbi5pbyIsImlkX3NlbGxlciI6N30.ICtC1xA5VrHYo2IXZVmtqqaMAP9cicBdhYtcHNSqB8Y";
            String transactionId = args[0];
            String signature = Hmac.calcHmacSha256(privateKey, transactionId);

            OkHttpClient client = new OkHttpClient();
            FormBody body = new FormBody.Builder()
                    .add("apiKey", apiKey)
                    .add("transactionId", transactionId)
                    .add("signature", signature)
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
