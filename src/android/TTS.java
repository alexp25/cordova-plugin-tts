package com.wordsbaking.cordova.tts;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.UtteranceProgressListener;

import java.util.HashMap;
import java.util.Locale;
import android.os.Bundle; // Import for using Bundle


/*
    Cordova Text-to-Speech Plugin
    https://github.com/vilic/cordova-plugin-tts

    by VILIC VANE
    https://github.com/vilic

    MIT License
*/

public class TTS extends CordovaPlugin implements OnInitListener {

    public static final String ERR_INVALID_OPTIONS = "ERR_INVALID_OPTIONS";
    public static final String ERR_NOT_INITIALIZED = "ERR_NOT_INITIALIZED";
    public static final String ERR_ERROR_INITIALIZING = "ERR_ERROR_INITIALIZING";
    public static final String ERR_UNKNOWN = "ERR_UNKNOWN";

    boolean ttsInitialized = false;
    TextToSpeech tts = null;
    private CallbackContext storedCallbackContext; // Store callback context for use in listeners

    @Override
    public void initialize(CordovaInterface cordova, final CordovaWebView webView) {
        tts = new TextToSpeech(cordova.getActivity().getApplicationContext(), this);       
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext)
            throws JSONException {

        switch (action) {
            case "speak":
                storedCallbackContext = callbackContext; // Store the current callback context
                try {
                    speak(args, callbackContext);
                } catch (JSONException e) {
                    callbackContext.error("JSON error: " + e.getMessage());
                }
                return true;
            case "stop":
                stop(callbackContext);
                return true;
            default:
                callbackContext.error("Invalid action");
                return false; // Returning false indicates an unrecognized action
        }
    }

   @Override
    public void onInit(int status) {
        if (status != TextToSpeech.SUCCESS) {
            tts = null;
        } else {
            tts.setLanguage(Locale.US); // Example, adjust based on your needs
            tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String s) {
                    // do nothing
                }

                @Override
                public void onStop(String utteranceId, boolean interrupted) {
                    // Called when an utterance has been stopped while in progress or flushed from the synthesis queue.                    
                     if (storedCallbackContext != null) {
                        storedCallbackContext.success(); // Notify success on the callback stored during 'speak'
                    }
                }

                @Override
                public void onDone(String utteranceId) {
                    if (storedCallbackContext != null) {
                        storedCallbackContext.success(); // Notify success on the callback stored during 'speak'
                        storedCallbackContext = null; // Clear the stored callback context
                    }
                }

                @Override
                public void onError(String utteranceId) {
                    if (storedCallbackContext != null) {
                        storedCallbackContext.error(ERR_UNKNOWN); // Notify error
                        storedCallbackContext = null; // Clear the stored callback context
                    }
                }
            });
            ttsInitialized = true;
        }
    }

    private void speak(JSONArray args, CallbackContext callbackContext) throws JSONException {

        // storedCallbackContext.error(STATUS_FINISHED); // for some reason .success never reaches the Ionic app
        if (!ttsInitialized) {
            storedCallbackContext.error(ERR_NOT_INITIALIZED);
            return;
        }   

        JSONObject params = args.optJSONObject(0);
        if (params == null) {
            storedCallbackContext.error(ERR_INVALID_OPTIONS);
            return;
        }

        String text = params.optString("text", "");
        String localeStr = params.optString("locale", "en-US");
        double rate = params.optDouble("rate", 1.0);
        double volume = params.optDouble("volume", 1.0);

        if (text.isEmpty()) {
            storedCallbackContext.error(ERR_INVALID_OPTIONS);
            return;
        }

        Locale locale = new Locale(localeStr.split("-")[0], localeStr.split("-")[1]);
        tts.setLanguage(locale);
        tts.setPitch((float) 1.0); // Example, adjust as needed
        tts.setSpeechRate((float) rate);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bundle paramsBundle = new Bundle();
            paramsBundle.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, (float) volume);
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, paramsBundle, storedCallbackContext.getCallbackId());
        } else {
            // Fallback for older versions, deprecated after API 21
            HashMap<String, String> ttsParams = new HashMap<>();
            ttsParams.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, storedCallbackContext.getCallbackId());
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, ttsParams);
        }
       
    }

    private void stop(CallbackContext callbackContext) {
        if (tts != null) {
            tts.stop();
            callbackContext.success(); // Signal that the stop action was successful
        } else {
            callbackContext.error("TTS not initialized"); // Error if TTS wasn't initialized properly
        }
    }
}