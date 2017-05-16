package com.apashnov.cwgram.client;

import com.apashnov.cwgram.client.model.ChatImpl;
import com.apashnov.cwgram.client.model.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.telegram.bot.kernel.database.DatabaseManager;
import org.telegram.bot.structure.Chat;
import org.telegram.bot.structure.IUser;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by apashnov on 15.05.2017.
 */
public class DatabaseManagerInMemory implements DatabaseManager {
    Map<Integer, IUser> userCache = new ConcurrentHashMap<>();
    Map<Integer, Chat> chatCache = new ConcurrentHashMap<>();
    Map<Integer, int[]>  differencesData = new ConcurrentHashMap<>();

    /**
     * Private constructor (due to Singleton)
     */
    private DatabaseManagerInMemory() {
//        connetion = new ConnectionDB();
//        final int currentVersion = connetion.checkVersion();
//        BotLogger.info(LOGTAG, "Current db version: " + currentVersion);
//        if (currentVersion < CreationStrings.version) {
//            recreateTable(currentVersion);
//        }
    }

    private static final DatabaseManagerInMemory instance = new DatabaseManagerInMemory();

    public static DatabaseManagerInMemory getInstance(){
        return instance;
    }

    /**
     * Gets an user by id
     *
     * @param userId ID of the user
     * @return User requested or null if it doesn't exists
     * @see User
     */
    @Override
    public @Nullable IUser getUserById(int userId) {
        return userCache.get(userId);
    }

    /**
     * Adds an user to the database
     *
     * @param user User to be added
     * @return true if it was added, false otherwise
     * @see User
     */
    public boolean addUser(@NotNull User user) {
        IUser iUser = userCache.get(user.getUserId());
        userCache.put(user.getUserId(), user);
        return iUser != null;
    }

    public boolean updateUser(@NotNull User user) {
        IUser iUser = userCache.get(user.getUserId());
        userCache.put(user.getUserId(), user);
        return iUser != null;
    }

    @Override
    public @Nullable Chat getChatById(int chatId) {
        return chatCache.get(chatId);
    }

    /**
     * Adds a chat to the database
     *
     * @param chat User to be added
     * @return true if it was added, false otherwise
     * @see User
     */
    public boolean addChat(@NotNull ChatImpl chat) {
        return chatCache.put(chat.getId(),chat) != null;
    }

    public boolean updateChat(ChatImpl chat) {
        return chatCache.put(chat.getId(),chat) != null;
    }

    @Override
    public @NotNull HashMap<Integer, int[]> getDifferencesData() {
        final HashMap<Integer, int[]> differencesDatas = new HashMap<>();
        System.out.println("need to debug");
//        try {
//            final PreparedStatement preparedStatement = connetion.getPreparedStatement("SELECT * FROM DifferencesData");
//            final ResultSet result = preparedStatement.executeQuery();
//            while (result.next()) {
//                final int[] differencesData = new int[3];
//                differencesData[0] = result.getInt("pts");
//                differencesData[1] = result.getInt("date");
//                differencesData[2] = result.getInt("seq");
//                differencesDatas.put(result.getInt("botId"), differencesData);
//            }
//            result.close();
//        } catch (SQLException e) {
//            BotLogger.error(LOGTAG,e);
//        }
        return new HashMap<>(differencesData);
    }

    @Override
    public boolean updateDifferencesData(int botId, int pts, int date, int seq) {
//        int updatedRows = 0;
//        try {
//            final PreparedStatement preparedStatement = connetion.getPreparedStatement("REPLACE INTO DifferencesData (botId, pts, date, seq) VALUES (?, ?, ?, ?);");
//            preparedStatement.setInt(1, botId);
//            preparedStatement.setInt(2, pts);
//            preparedStatement.setInt(3, date);
//            preparedStatement.setInt(4, seq);
//            updatedRows = preparedStatement.executeUpdate();
//        } catch (SQLException e) {
//            BotLogger.error(LOGTAG,e);
//        }
//        return updatedRows > 0;
        return differencesData.put(botId, new int[]{pts, date, seq}) != null;
    }

    @Override
    protected void finalize() throws Throwable {
//        connetion.closeConexion();
//        super.finalize();
    }

}

