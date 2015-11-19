package v5.cmmc;

import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import v5.cmmc.helper.MQTTHelper;
import v5.cmmc.helper.MQTTHelper_;

import static android.os.Looper.getMainLooper;

/**
 * Created by nat on 11/18/15 AD.
 */

public class MqttModule extends ReactContextBaseJavaModule {
    public ReactApplicationContext mContext;
    public static final String TAG = MqttModule.class.getSimpleName();

    public MqttModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mContext = reactContext;
    }

    @Override
    public String getName() {
        return "CMMCMQTTModule";
    }

    @ReactMethod
    public void show(String message) {
        Toast.makeText(getReactApplicationContext(), message, Toast.LENGTH_LONG).show();
    }


    private void sendEvent(ReactContext reactContext,
                           String eventName,
                           @Nullable Object params) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }

    @ReactMethod
    public void blah(final Callback callback) {
        Handler handler = new Handler(getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                callback.invoke("" + System.currentTimeMillis());
            }
        });

    }

    public void prepareConnection(String host, int port, String clientId, String user, String pass) {
    }

//    @ReactMethod
//    public void connectMqtt(final Callback callback) {
//        MQTTHelper_.getInstance_(mContext).connectMqtt(new MqttCallback() {
//            @Override
//            public void connectionLost(Throwable throwable) {
//                Log.d(TAG, "connectionLost: ");
//                sendEvent(mContext, "connectionLost", throwable);
//            }
//
//            @Override
//            public void messageArrived(String s, final MqttMessage mqttMessage) throws Exception {
//                Log.d(TAG, "messageArrived: " + s.toCharArray());
//            }
//
//            @Override
//            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
//                Log.d(TAG, "deliveryComplete: ");
//            }
//        });
//
//    }

    @ReactMethod
    public void connect(ReadableMap configMap, final Callback callback) {
        String clientId =  configMap.getString("clientId");
        String user = configMap.getString("username");
        String pass = configMap.getString("password");
        String host = configMap.getString("host");
        int port = configMap.getInt("port");

        MQTTHelper mqttHelper = MQTTHelper_.getInstance_(mContext);
        mqttHelper.setPort(port);
        mqttHelper.setHost(host);

        mqttHelper.setClientId(clientId);
        mqttHelper.setAuth(user, pass);

        mqttHelper.createConnection(new MQTTHelper.MQTTHelperCallback() {
            @Override
            public void onReady(MqttClient mqttClient) {
                Log.d(TAG, "onReady: 114");
                callback.invoke(null, "OK");
            }

            @Override
            public void onError(MqttException e) {
                Log.d(TAG, "onError: 120");
                callback.invoke(e.getMessage());
            }

            @Override
            public void onUnkownError(Exception e) {
                callback.invoke(e.getMessage());
                Log.d(TAG, "onUnkownError: 125");
            }
        });

    }

    @ReactMethod
    public void subscribe(String topic) {
        int qos = 0;

        MQTTHelper_.getInstance_(mContext).subscribe(topic, qos, new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {
                sendEvent(mContext, "connectionLost", throwable.getMessage());
            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {

                WritableMap params = new WritableNativeMap();
                String message = new String(mqttMessage.getPayload());
                params.putString("topic", topic);
                params.putString("message", message);

                sendEvent(mContext, "messageArrived", params);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                sendEvent(mContext, "deliveryComplete", "COMPLETE");
            }
        });
    }

    @ReactMethod
    public void recvObject(ReadableMap objectMap) {
        sendEvent(mContext, "recvObject", objectMap.getString("test"));
        inspect(objectMap.getClass());
        Log.d(TAG, "recvObject: ");
    }


    static <T> void inspect(Class<T> klazz) {
        Field[] fields = klazz.getDeclaredFields();
        System.out.printf("%d fields:%n", fields.length);
        for (Field field : fields) {
            System.out.printf("%s %s %s%n",
                    Modifier.toString(field.getModifiers()),
                    field.getType().getSimpleName(),
                    field.getName()
            );
        }
    }
}
