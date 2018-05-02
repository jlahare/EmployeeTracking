package com.yash.employeetrack;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.net.URI;

/**
 * Created by vipin.jain on 5/1/2018.
 */

public class Subscriber implements MqttCallback {

    private final int qos = 1;
    private String topic = "test";
    private MqttClient client;

    Context context;


    public Subscriber(Context context) throws MqttException
    {
        this.context = context;
        String host = String.format("tcp://%s:%d", "m14.cloudmqtt.com",19344);
        //String[] auth = this.getAuth(uri);
        String username = "xppieoxr";
        String password = "72xlRZiP6Z-i";
        String clientId = "iBeacon";
       /* if (!uri.getPath().isEmpty()) {
            this.topic = uri.getPath().substring(1);
        }*/
        this.topic="iBeacon";

        MqttConnectOptions conOpt = new MqttConnectOptions();
        conOpt.setCleanSession(true);
        conOpt.setUserName(username);
        conOpt.setPassword(password.toCharArray());

        this.client = new MqttClient(host, clientId, new MemoryPersistence());
        this.client.setCallback(this);
        this.client.connect(conOpt);

        this.client.subscribe(this.topic, qos);
    }

    private String[] getAuth(URI uri) {
        String a = uri.getAuthority();
        String[] first = a.split("@");
        return first[0].split(":");
    }

    public void sendMessage(String payload) throws MqttException {
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(qos);
        this.client.publish(this.topic, message); // Blocking publish
    }

    /**
     * @see MqttCallback#connectionLost(Throwable)
     */
    public void connectionLost(Throwable cause) {
        System.out.println("Connection lost because: " + cause);
        Log.e("MQTT" , "Connection lost : " + cause);
       //System.exit(1);
    }

    /**
     * @see MqttCallback#deliveryComplete(IMqttDeliveryToken)
     */
    public void deliveryComplete(IMqttDeliveryToken token) {
    }

    /**
     * @see MqttCallback#messageArrived(String, MqttMessage)
     */
    public void messageArrived(String topic, MqttMessage message) throws MqttException
    {
        System.out.println(String.format("[%s] %s", topic, new String(message.getPayload())));

    }

    /*public static void main(String[] args) throws MqttException, URISyntaxException {
        Subscriber s = new Subscriber(System.getenv("CLOUDMQTT_URL"));
        s.sendMessage("Hello");
        s.sendMessage("Hello 2");
    }*/
}

