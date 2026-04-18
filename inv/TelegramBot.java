package adventure;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;

public class TelegramBot {
    private static final String BOT_TOKEN = "7718341404:AAF4ipog52tvQAbjgVZ241OpMJ9la9oF_Tc"; // 🔹 Вставь свой токен бота
    private static final String CHAT_ID = "-1001789913473";     // 🔹 Вставь ID группы
    private static int lastUpdateId = 0; // ❗ Запоминаем ID последнего обработанного сообщения

    public static void startBot(MinecraftServer server) {
        new Thread(() -> {
            while (true) {
                try {
                    // Запрашиваем только новые сообщения
                    URL url = new URL("https://api.telegram.org/bot" + BOT_TOKEN + "/getUpdates?offset=" + (lastUpdateId + 1));
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");

                    Scanner scanner = new Scanner(conn.getInputStream());
                    StringBuilder response = new StringBuilder();
                    while (scanner.hasNext()) {
                        response.append(scanner.nextLine());
                    }
                    scanner.close();

                    // Обрабатываем команду
                    processTelegramUpdates(response.toString(), server);

                    // Ждём 3 секунды перед следующим запросом
                    Thread.sleep(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static void processTelegramUpdates(String response, MinecraftServer server) {
        if (!response.contains("\"update_id\":")) return;

        // Найдём последний update_id
        String[] updateParts = response.split("\"update_id\":");
        for (int i = 1; i < updateParts.length; i++) {
            int updateId = Integer.parseInt(updateParts[i].split(",")[0].trim());

            // Обрабатываем только новые команды
            if (updateId > lastUpdateId) {
                lastUpdateId = updateId;

                // Проверяем, есть ли команда "/inventory <ник>"
                if (response.contains("\"text\":\"/inventory ")) {
                    String[] parts = response.split("\"text\":\"/inventory ");
                    if (parts.length > 1) {
                        String playerName = parts[1].split("\"")[0].trim();
                        checkInventory(playerName, server);
                    }
                }
            }
        }
    }

    private static void checkInventory(String playerName, MinecraftServer server) {
        ServerPlayer player = server.getPlayerList().getPlayerByName(playerName);
        if (player == null) {
            sendMessage("❌ Игрок не найден: " + playerName);
            return;
        }

        // Собираем инвентарь игрока
        StringBuilder inventoryMessage = new StringBuilder("🎒 Инвентарь " + playerName + ":\n");
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (!item.isEmpty()) {
                inventoryMessage.append("• ").append(item.getCount()).append("x ")
                    .append(item.getHoverName().getString()).append("\n");
            }
        }

        // Отправляем сообщение в Telegram
        sendMessage(inventoryMessage.toString());
    }

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
            os.write(jsonPayload.getBytes());
            os.flush();
            os.close();

            conn.getInputStream(); // Читаем ответ, чтобы не было ошибок
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
