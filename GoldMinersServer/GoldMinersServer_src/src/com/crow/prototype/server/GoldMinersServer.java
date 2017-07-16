package com.crow.prototype.server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Scanner;
import javax.swing.Timer;


public class GoldMinersServer {
    
    public static final int DROP_TIME = 10;
    public static final int MAP_WIDTH = 48;
    public static final int MAP_HEIGHT = 50;
    public static final int BUFFER = 1024;
    public static final int MINING_DELAY = 1500;
    public static final int DROP_CHECK_PERIOD = 3000;
    public static final int LARGE_UPDATE_PERIOD = 5000;
    
    private Scanner input;
    private String command;
    private Thread serverThread;
    private boolean serverUp;
    private int port;
    
    private byte[] receiveData;
    private byte[] sendData;
    private String receivedStr;
    private String sendStr;
    private Scanner reader;
    private String prefix;
    
    private DatagramSocket serverSocket;
    private DatagramPacket receivePacket;
    private DatagramPacket sendPacket;
    private InetAddress senderAddress;
    private ArrayList<Client> clientList;
    private int senderPort;
    private Client senderClient;
  
    private byte[][] map;
    private String mapData;
    private String playerData;
    
    private Timer checkTimer;
    private Timer updateTimer;
    
    private String chatLine;
    private ArrayList<ScoreData> scores;
    private long gameStartTime;
    private long gameEndTime;
    
    private class Client {
        InetAddress address;
        int port;
        int offDuration;
        PlayerData player;
        public Client(InetAddress address, int port, PlayerData player) {
            this.address = address;
            this.port = port;
            this.player = player;
        }
        @Override
        public String toString() {
            return player.name + senderAddress + ":" + senderPort;
        }
    }
        
    public static void main(String[] args) throws Exception {
        new GoldMinersServer();
    }
    
    public GoldMinersServer() {
        initialize();
    }
    
    private void initialize() {
        input = new Scanner(System.in);
        System.out.print("Enter PORT to start the server: ");
        port = input.nextInt();
        initServer();
        if (!serverUp) { return; }
        initGame();
        startListening();
        startClientChecking();
        startLargeUpdates();
        listenCommands();
    }
    
    private void initServer() {
        try {
            serverSocket = new DatagramSocket(port);
            clientList = new ArrayList<Client>();
            serverUp = true;
        } catch (SocketException e) {
            System.out.println("Connection failed.");
            e.printStackTrace();
        }
    }
    
    private void initGame() {
        map = MapManager.createMap(MAP_WIDTH, MAP_HEIGHT);
        scores = new ArrayList<ScoreData>();
    }
    
    private void startListening() {
        serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                listenClients();
            }
        });
        serverThread.start();
    }
        
    private void startClientChecking() {
        checkTimer = new Timer(DROP_CHECK_PERIOD, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkClients();
            }
        });
        checkTimer.start();
    }
    
    private void startLargeUpdates() {
        updateTimer = new Timer(LARGE_UPDATE_PERIOD, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                largeUpdateAll();
                checkEndGame();
            }
        });
        updateTimer.start();
    }
    
    private synchronized void checkClients() {
        Iterator<Client> iter = clientList.iterator();
        while(iter.hasNext()) {
            Client c = iter.next();
            if (++c.offDuration >= DROP_TIME) {
                System.out.println("<LOG> Client dropped: " + c);
                sendToAllClients("DROP " + c.player.name);
                iter.remove();
            }
        }
    }
    
    private void listenClients() {
        while (serverUp)
        {
            try {
                receiveData = new byte[BUFFER];
                receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                receivedStr = new String(receivePacket.getData()).trim();
                senderAddress = receivePacket.getAddress();
                senderPort = receivePacket.getPort();
                senderClient = getClient(senderAddress, senderPort);
                reader = new Scanner(receivedStr);
                prefix = reader.next();
                sendStr = null;
                
                if (prefix.equals("CONNECT")) {
                    if (senderClient == null) {
                        String playerName = reader.next();
                        boolean isMale = reader.nextBoolean();
                        if (!isSuitable(playerName)) {
                            sendStr = "CLIENT_DENIED";
                        } else {
                            PlayerData senderPlayer = new PlayerData(playerName, isMale);
                            senderClient = new Client(senderAddress, senderPort, senderPlayer);
                            clientList.add(senderClient);
                            if (!scoreExists(playerName)) {
                                scores.add(new ScoreData(playerName));
                            }
                            System.out.println("<LOG> Client connected: " + senderClient);
                            largeUpdate(senderClient);
                            sendPlayerData(senderClient);
                            sendStr = "DONE";
                            sendToOtherClients("JOIN " + playerName + " " + isMale, senderClient);
                        }
                    }
                    else {
                        sendStr = "ALREADY_IN";
                    }
                }
                
                else if (senderClient == null) {
                    sendStr = "SHOULD_RECONNECT";
                }
                
                else if (prefix.equals("SEND_PING")) {
                    sendStr = "PING " + reader.nextLong();
                    senderClient.offDuration = 0;
                }
                
                else if (prefix.equals("POSITION")) {
                    senderClient.player.tileX = reader.nextInt();
                    senderClient.player.tileY = reader.nextInt();
                    sendToOtherClients("POSITION " +  senderClient.player.name + " " +
                        senderClient.player.tileX + " " + senderClient.player.tileY, senderClient);
                }

                else if (prefix.equals("CHAT")) {
                    chatLine = reader.nextLine();
                    sendToAllClients("CHAT " + senderClient.player.name + " " + chatLine);
                    System.out.println("> CHAT " + senderClient.player.name + ": " + chatLine);
                }

                else if (prefix.equals("LEAVE")) {
                    clientList.remove(senderClient);
                    sendToAllClients("LEAVE " + senderClient.player.name);
                    System.out.println("<LOG> Client left: " + senderClient);
                }

                else if (prefix.equals("START_MINING")) {
                    sendToOtherClients(receivedStr, senderClient);
                    setStoppingTimer(reader.nextInt(), reader.nextInt());
                }

                else if (prefix.equals("STOP_MINING")) {
                    sendToOtherClients(receivedStr, senderClient);
                }

                else if (prefix.equals("MINED")) {
                    int minedX = reader.nextInt();
                    int minedY = reader.nextInt();
                    int amount = reader.nextInt();
                    senderClient.player.giveCoin(amount);
                    map[minedX][minedY] = MapManager.EMPTY;
                    updateScore(senderClient.player.name, amount);
                    sendToOtherClients("MINED " + minedX + " " + minedY, senderClient);
                }
                
                else {
                    System.out.println("<ERROR> Invalid request came: " + receivedStr);
                    sendStr = "UNKNOWN_COMMAND";
                }
                
                reader.close();
                
                if (sendStr != null) {
                    sendData = sendStr.getBytes();
                    sendPacket = new DatagramPacket(sendData, sendData.length, senderAddress, senderPort);
                    serverSocket.send(sendPacket);
                }
                
            } catch (IOException e) {
                if (e.getMessage().equals("socket closed")) {
                    System.out.println("<LOG> The server is closed.");
                } else {
                    System.out.println("A connection error occured.");
                    e.printStackTrace();
                }
            }
        }
    }
    
    private Client getClient(InetAddress address, int port) {
        for (Client c : clientList) {
            if (c.address.equals(address) && c.port == port) {
                return c;
            }
        }
        return null;
    }
    
    private void listenCommands() {
        input.nextLine();
        System.out.println("Server Commands: 'HELP', 'BEGIN', 'LIST', 'NOTIFY message', 'KICK PlayerName', 'CLOSE'.");
        System.out.println("<LOG> The server is started.");
        do {
            command = input.nextLine().trim();
            if (!serverUp) {}
            else if (command.equalsIgnoreCase("HELP")) {
                printCommandList();
            } else if (command.equalsIgnoreCase("BEGIN")) {
                beginGame();
            } else if (command.length() >= "NOTIFY ".length() && 
                command.substring(0, "NOTIFY ".length()).equalsIgnoreCase("NOTIFY ")) {
                sendToAllClients("NOTIFY " + command.substring("NOTIFY ".length()));
            } else if (command.equalsIgnoreCase("LIST")) {
                printClientList();
            } else if (command.length() >= "KICK ".length() && 
                command.substring(0, "KICK ".length()).equalsIgnoreCase("KICK ")) {
                kickPlayer(command.substring("KICK ".length()));
            } else if (!command.equalsIgnoreCase("CLOSE") && serverUp){
                System.out.println("Unknown command. Type HELP to get the command list.");
            }
        } while (!command.equalsIgnoreCase("CLOSE") && serverUp);
        if (command.equalsIgnoreCase("CLOSE")) {
            System.out.println("Closing the server...");
        }
        closeServer();
    }
    
    private void printCommandList() {
        System.out.printf("BEGIN           - Starts the game by opening the gate blocks around the players.%n" +
                          "LIST            - Lists all the clients connected to the server.%n" +
                          "NOTIFY message  - Sends the given message to all of the clients.%n" +
                          "KICK PlayerName - Kicks the player with the given name from the server.%n" +
                          "CLOSE           - Drops all the connected clients and closes the server.%n");
    }
    
    private void kickPlayer(String badName) {
        Client c;
        Client badClient = null;
        Iterator<Client> iter = clientList.iterator();
        while(iter.hasNext()) {
            c = iter.next();
            if (c.player.name.equals(badName)) {
                badClient = c;
            }
        }
        if (badClient == null) {
            System.out.println("No client was found with the given name.");
        } else {
            clientList.remove(badClient);
            sendToAllClients("KICK " + badClient.player.name);
            System.out.println("<LOG> Client kicked: " + badClient);
        }
    }
    
    private void sendToAllClients(String publicStr) {
        sendData = publicStr.getBytes();
        Iterator<Client> iter = clientList.iterator();
        while(iter.hasNext()) {
            try {
                Client c = iter.next();
                sendPacket = new DatagramPacket(sendData, sendData.length, c.address, c.port);
                serverSocket.send(sendPacket);
            } catch (Exception e) {
                if (gameEndTime == 0) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private void sendToOtherClients(String publicStr, Client discluded) {
        sendData = publicStr.getBytes();
        Iterator<Client> iter = clientList.iterator();
        while(iter.hasNext()) {
            try {
                Client c = iter.next();
                if (!c.equals(discluded)) {
                    sendPacket = new DatagramPacket(sendData, sendData.length, c.address, c.port);
                    serverSocket.send(sendPacket);
                }
            } catch (IOException e) { e.printStackTrace(); }
        }
    }
    
    private void printClientList() {
        Iterator<Client> iter = clientList.iterator();
        if (iter.hasNext()) {
            System.out.println("--Connected Clients--");
        } else {
            System.out.println("No client is connected to the server.");
        }
        Client c;
        while(iter.hasNext()) {
            c = iter.next();
            System.out.println(c);
        }
    }
    
    private boolean isSuitable(String name) {
        Iterator<Client> iter = clientList.iterator();
        while(iter.hasNext()) {
            if (iter.next().player.name.equals(name)) {
                return false;
            }
        }
        return true;
    }
    
    private void sendPlayerData(Client c) throws IOException {
        playerData = "";
        Iterator<Client> iter = clientList.iterator();
        PlayerData p;
        while(iter.hasNext()) {
            p = iter.next().player;
            playerData += p.getPublicData() + " ";
        }
        sendStr = "LARGE_UPDATE_PLAYER " + playerData;
        sendData = sendStr.getBytes();
        serverSocket.send(new DatagramPacket(sendData, sendData.length, c.address, c.port));
    }
    
    private void largeUpdate(Client c) throws IOException {
        mapData = MapManager.getMapData(map);
        sendStr = "LARGE_UPDATE_MAP " + mapData;
        sendData = sendStr.getBytes();
        serverSocket.send(new DatagramPacket(sendData, sendData.length, c.address, c.port));
    }
    
    private void largeUpdateAll() {
        Iterator<Client> iter = clientList.iterator();
        while(iter.hasNext()) {
            try {
                Client c = iter.next();
                largeUpdate(c);
            } catch (IOException e) { e.printStackTrace(); }
        }
    }
    
    private class TimerReference {
        Timer timer;
    }
    
    private class MineStopper implements ActionListener {
        private TimerReference timerRef;
        private int x;
        private int y;
        public MineStopper(TimerReference timerRef, int x, int y) {
            this.timerRef = timerRef;
            this.x = x;
            this.y = y;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            sendToAllClients("STOP_MINING " + x + " " + y);
            timerRef.timer.stop();
        }
    }
    
    private void setStoppingTimer(int startX, int startY) {
        TimerReference timerRef = new TimerReference();
        Timer stoppingTimer = new Timer(MINING_DELAY + 300, new MineStopper(timerRef, startX, startY));
        timerRef.timer = stoppingTimer;
        stoppingTimer.start();
    }
    
    private void updateScore(String name, int amount) {
        Iterator<ScoreData> iter = scores.iterator();
        while(iter.hasNext()) {
            ScoreData score = iter.next();
            if (score.playerName.equals(name)) {
                score.extractNum += 1;
                score.goldNum += amount;
                return;
            }
        }
    }
    
    private boolean scoreExists(String name) {
        Iterator<ScoreData> iter = scores.iterator();
        while(iter.hasNext()) {
            if (iter.next().playerName.equals(name)) {
                return true;
            }
        }
        return false;
    }
    
    private void beginGame() {
        gameStartTime = System.currentTimeMillis();
        MapManager.beginMap(map);
        largeUpdateAll();
        sendToAllClients("BEGIN");
        System.out.println("<LOG> The game has begun.");
    }
    
    private void checkEndGame() {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j] == MapManager.GOLD) {
                    return;
                }
            }
        }
        endGame();
    }
    
    private void endGame() {
        scores.sort(new Comparator<ScoreData>() {
            @Override
            public int compare(ScoreData s1, ScoreData s2) {
                return new Integer(s2.extractNum).compareTo(s1.extractNum);
            }
        });
        gameEndTime = System.currentTimeMillis();
        String endStr = "END ";
        for (ScoreData sd : scores) {
            endStr += sd.getData() + " ";
        }
        sendToAllClients(endStr);
        sendToAllClients(endStr);
        closeServer();
        System.out.println("<LOG> The game is over with " +
            ((gameEndTime - gameStartTime) / (1000 * 60)) + " minutes played.");
        System.out.println("   Player Name | Mined Ores | Total Coin");
        for (int i = 0; i < scores.size(); i++) {
            ScoreData sd = scores.get(i);
            System.out.println((i+1) + ". " + sd.playerName + " | " +
                sd.extractNum + " | " + sd.goldNum);
        }
        System.out.println("Press enter to exit... ");
    }
    
    private void closeServer() {
        checkTimer.stop();
        updateTimer.stop();
        if (gameEndTime == 0) {
            sendToAllClients("SERVER_CLOSED");
        }
        serverUp = false;
        serverSocket.close();
        serverThread.interrupt();
    }
    
}
