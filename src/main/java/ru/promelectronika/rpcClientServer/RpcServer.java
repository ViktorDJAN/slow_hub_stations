package ru.promelectronika.rpcClientServer;


import lombok.Getter;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.value.*;
import ru.promelectronika.util_stuff.ColorTuner;
import ru.promelectronika.dataBases.ControllersParamsDataBase;
import ru.promelectronika.dataBases.CtrlEnMeterParamsDataBase;
import ru.promelectronika.enums.MessageEnumType;
import ru.promelectronika.logHandler.LogHandler;


import java.io.IOException;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

@Getter
public class RpcServer {
    private final Selector selector;
    private final ServerSocketChannel serverSocketChannel;
    private SocketChannel client;
    private final SelectionKey selectKey;
    Map<String, Double> map = new HashMap<>();
    //    private  ControllerParamsDto model3Controller;
    private final ByteBuffer buffer = ByteBuffer.allocate(1024);

    // todo: change logics so that RpcServer constructor only responses for the creating logic
    public RpcServer(String address, int port) throws IOException {
        serverSocketChannel = ServerSocketChannel.open();
        selector = Selector.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(address, port));
        int ops = serverSocketChannel.validOps();
        selectKey = serverSocketChannel.register(selector, ops, null);
//        model3Controller = new ControllerParamsDto();      todo   / begin from here , change logic of that object you need to refer directly to tha map

    }


    public void start() throws IOException {
        int i = selector.select(10);
        if (i == 0) {
            return;
        }
        Set<SelectionKey> selectionKeys = selector.selectedKeys();
        Iterator<SelectionKey> selectionKeyIterator = selectionKeys.iterator();

        while (selectionKeyIterator.hasNext()) {
            SelectionKey aKey = selectionKeyIterator.next();
            if (aKey.isAcceptable()) {
                accept();
            } else if (aKey.isReadable()) {
                try {
                    readMessage((SocketChannel) aKey.channel());
                } catch (RuntimeException e) {
                    aKey.cancel();
                    aKey.channel().close();
                    System.out.println("Execption " + e);
                }
            }
            selectionKeyIterator.remove();
        }
    }

    // todo need to add under the special name controller dto
    public void accept() throws IOException {
        client = serverSocketChannel.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
        ColorTuner.whiteBackgroundBlueText("Connection Accepted: " + client.getRemoteAddress());
        ColorTuner.whiteBackgroundBlueText("Is connected: " + client.isConnected());

//        // Filling dataBase where ID = last three digits of IP_address
//        String clientAddress = client.socket().getInetAddress().toString();
//        String clientID = clientAddress.substring(clientAddress.length() - 3);
//        ControllersParamsDataBase.map.put(clientID, new ControllerParamsDto());


    }

    private byte[] read(SocketChannel client) throws IOException {
        int len = client.read(buffer);
        if (len == -1) {
            len = 0;
        }
        byte[] data = Arrays.copyOf(buffer.array(), len);
        Arrays.fill(buffer.array(), (byte) 0);
        buffer.clear();
        return data;
    }

    private void write(byte[] writeBuf, SocketChannel channel) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(writeBuf);
        channel.write(buffer);
    }

    public void sendMessage(int msgId, SocketChannel channel) throws IOException {
        MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
        packer.packArrayHeader(4)
                .packInt(MessageEnumType.RESPONSE)
                .packInt(msgId)
                .packNil()
                .packNil();
        packer.flush();
        this.write(packer.toByteArray(), channel);
    }

    public ArrayList<Object> arrayHandler(ArrayValue value) {
        ArrayList<Object> arrayObj = new ArrayList<>();
        for (Value v : value) {
            switch (v.getValueType()) {
                case NIL:
                    arrayObj.add(null);
                    break;
                case BOOLEAN:
                    boolean b = v.asBooleanValue().getBoolean();
                    arrayObj.add(b);
                    break;
                case INTEGER:
                    IntegerValue iv = v.asIntegerValue();
                    if (iv.isInIntRange()) {
                        int i = iv.toInt();
                        arrayObj.add(i);
                    } else if (iv.isInLongRange()) {
                        long l = iv.toLong();
                        arrayObj.add(l);
                    } else {
                        BigInteger i = iv.toBigInteger();
                        arrayObj.add(i);
                    }
                    break;
                case FLOAT:
                    FloatValue fv = v.asFloatValue();
                    if (fv.getValueType().equals(ValueType.FLOAT)) {
                        float f = fv.toFloat();   // use as float
                        arrayObj.add(f);
                    } else {
                        double d = fv.toDouble(); // use as double
                        arrayObj.add(d);
                    }
                    break;
                case STRING:
                    String s = v.asStringValue().asString();
                    arrayObj.add(s);
                    break;

                case ARRAY:
                    ArrayValue a = v.asArrayValue();
                    arrayObj.add(arrayHandler(a));
                    break;
            }
        }
        return arrayObj;
    }

    public void readMessage(SocketChannel channel) {

        byte[] msg;
        try {
            msg = this.read(channel);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        if (msg.length > 0) {
            MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(msg);
            while (true) {
                try {
                    if (!unpacker.hasNext()) break;
                } catch (IOException e) {
                    LogHandler.logThrowableServer(e);
                }
                Value v = null;
                try {
                    v = unpacker.unpackValue();
                } catch (IOException e) {
                    LogHandler.logThrowableServer(e);
                }
                if (v.getValueType() == ValueType.ARRAY) {
                    ArrayValue vArray = v.asArrayValue();
                    ArrayList<Object> arrayObject;
                    arrayObject = arrayHandler(vArray);
                    int msgType = 0;
                    int msgId = -1;
                    String method = null;
                    ArrayList<Object> arguments = null;
                    try {
                        if ((int) arrayObject.get(0) == MessageEnumType.REQUEST) {

                            msgId = (int) arrayObject.get(1);
                            method = (String) arrayObject.get(2);
                            arguments = (ArrayList<Object>) arrayObject.get(3);

                        } else if ((int) arrayObject.get(0) == MessageEnumType.NOTIFY) {
                            msgType = 2;
                            method = (String) arrayObject.get(1);
                            arguments = (ArrayList<Object>) arrayObject.get(2);
                        }

                        String address = channel.socket().getInetAddress().toString();

                        Integer clientID = Integer.parseInt(address.substring(11));


                        // todo: take out cases names and put 'em  into a new class with constants

                        switch (Objects.requireNonNull(method)) {
                            case "rpcPing":
//                                    rpcPing(arguments, address);
                                rpcPing(arguments, clientID);
                                break;
                            case "SET_FW_VERSION": //Передача текущей версии ПО контроллера
                                rpcSetFwVersion(arguments, clientID);
                                break;
                            case "SET_PROTOCOL_VERSION":// Передача текущей версии протокола обмена с электромобилем
                                rpcSetProtocolVersion(arguments, clientID);
                                break;
                            case "SET_SECC_CURRENT_STATE": // Передача текущей стадии зарядной сессии.
                                rpcSetSeccCurrentState(arguments, clientID);
                                break;

                            case "LOGGER":
                                LogHandler.loggerServer.info(" READ: " + arguments);
                            case "POWER_VALUE":
                                System.out.println(clientID + ": " + arguments);
//                                int controllerId = retrieveControllerId();
                                retrieveEnergyMeterParams(arguments, clientID);
                        }

                        if (msgType == MessageEnumType.REQUEST) {
                            try {
                                sendMessage(msgId, channel);
                            } catch (IOException e) {
                                LogHandler.logThrowableServer(e);
                            }
                        }
                    } catch (ClassCastException e) {
                        ColorTuner.redBackgroundBlackText("EXCEPTION: " + arrayObject.get(0) + " " + "Incorrect message " + e.getMessage() + " " + arguments);

                    }
                }
            }
        }
    }

    private int retrieveControllerId() {
        try {
            if (client != null) {
                String string = client.getRemoteAddress().toString().substring(11, 14);
                int id = Integer.parseInt(string);
                return id;
            } else {
                return 0;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void retrieveEnergyMeterParams(ArrayList<?> objects, int controllerId) {
        String vL1 = "vL1", vL2 = "vL2", vL3 = "vL3", curL1 = "curL1", curL2 = "curL2", curL3 = "curL3";
        if (objects.size() == 6) {
            double value;
            map.put(vL1, value = (float) objects.get(0));
            map.put(vL2, value = (float) objects.get(1));
            map.put(vL3, value = (float) objects.get(2));
            map.put(curL1, value = (float) objects.get(3));
            map.put(curL2, value = (float) objects.get(4));
            map.put(curL3, value = (float) objects.get(5));
        }


        //Insertion into the map with Electrical params
        if (controllerId != 0) {
            CtrlEnMeterParamsDataBase.map.put(controllerId, map);
        }


    }


    private void closeClient(SocketChannel channel) throws IOException {
        ColorTuner.whiteBackgroundRedText("Client " + channel.getRemoteAddress() + " closed");
        channel.close();
    }

    /**
     * !NOTE - Parsing and Processing dto , setting and getting necessary params
     */


    private void rpcPing(ArrayList params, Integer clientID) {
        if (ControllersParamsDataBase.map.containsKey(clientID)) {
            ControllersParamsDataBase.map.get(clientID).setSelfConnectionInputState((Integer) params.get(0));
            ControllersParamsDataBase.map.get(clientID).setSelfConnectionOutputState((Integer) params.get(1));

            LogHandler.loggerServer.debug("rpcPing: {}", ControllersParamsDataBase.map.get(clientID).getRpcPing());
        }
    }

    private void rpcSetFwVersion(ArrayList params, Integer clientID) {
        if (ControllersParamsDataBase.map.containsKey(clientID)) {
            ControllersParamsDataBase.map.get(clientID).setVersion((String) params.get(0));
            LogHandler.loggerServer.info("rpcSetFwVersion: {}", ControllersParamsDataBase.map.get(clientID).getSetFwVersion());
        }
    }

    private void rpcSetProtocolVersion(ArrayList params, Integer clientID) {
        if (ControllersParamsDataBase.map.containsKey(clientID)) {
            ControllersParamsDataBase.map.get(clientID).setProtocolVersion((String) params.get(0));
            LogHandler.loggerServer.info("rpcSetProtocolVersion: {}", ControllersParamsDataBase.map.get(clientID).getSetProtocolVersion());
        }
    }

    private void rpcSetSeccCurrentState(ArrayList params, Integer clientID) {
        try {
            if (ControllersParamsDataBase.map.containsKey(clientID)) {
                ControllersParamsDataBase.map.get(clientID).setChargeState((int) params.get(0));
                ControllersParamsDataBase.map.get(clientID).setChargeStateProtocolSpecific((String) params.get(1));
                LogHandler.loggerServer.info("rpcSetSeccCurrentState: {}", ControllersParamsDataBase.map.get(clientID).getSetSeccCurrentState());
            }
        } catch (ClassCastException e) {
            LogHandler.logThrowableServer(e);
        }
    }


}
