package adventure;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class TelegramBot {
    private static final String BOT_TOKEN = "7718341404:AAF4ipog52tvQAbjgVZ241OpMJ9la9oF_Tc"; // 🔹 Твой токен бота
    private static final String CHAT_ID = "-1001789913473";     // 🔹 ID группы/чата

    public static void sendMessage(String message) {
        try {
            String urlString = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendMessage";
            String jsonPayload = "{\"chat_id\":\"" + CHAT_ID + "\",\"text\":\"" + message + "\"}";
            
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            
            OutputStream os = conn.getOutputStream();
            os.write(jsonPayload.getBytes(StandardCharsets.UTF_8));
            os.flush();
            os.close();
            
            conn.getInputStream(); // Читаем ответ, чтобы не было ошибок
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
