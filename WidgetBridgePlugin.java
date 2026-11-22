package com.nefes.app;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

/** Lets the web app talk to the home screen widget. */
@CapacitorPlugin(name = "WidgetBridge")
public class WidgetBridgePlugin extends Plugin {

    @PluginMethod
    public void setSummary(PluginCall call) {
        String json = call.getString("json", "{}");
        WidgetStore.setSummary(getContext(), json);
        NefesWidgetProvider.updateAll(getContext());
        call.resolve();
    }

    @PluginMethod
    public void takePending(PluginCall call) {
        JSObject result = new JSObject();
        result.put("json", WidgetStore.takePending(getContext()));
        call.resolve(result);
    }
}
