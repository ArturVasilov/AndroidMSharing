package ru.guar7387.sharinggoogle;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(android.R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSharing();
            }
        });
    }

    private void showSharing() {
        //on pre-M this shows chooser with list of intents
        //on M preview this will open the first app from ResolveInfo list
        startActivity(getAllShareIntent());
    }

    private Intent getAllShareIntent() {
        Intent promoIntent = getPromoIntent();
        Intent intent = new Intent(promoIntent);
        String chooserText = "Select app";
        List<Intent> targetedShareIntents = new ArrayList<>();
        List<ResolveInfo> resolves = getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resolves) {
            String packageName = resolveInfo.activityInfo.packageName;
            Intent targetedShareIntent = new Intent(promoIntent);
            //here we filter some apps
            //in fact issue can be reproduced even if list of resolveinfo hadn't changed after filtering
            if (!packageName.equals("com.facebook.katana")) {
                ComponentName componentName = new ComponentName(packageName, resolveInfo.activityInfo.name);
                targetedShareIntents.add(targetedShareIntent.setComponent(componentName));
            }
        }

        if (targetedShareIntents.isEmpty()) {
            return null;
        }
        Intent chooser = targetedShareIntents.remove(0);
        return Intent.createChooser(chooser, chooserText)
                .putExtra(Intent.EXTRA_INITIAL_INTENTS,
                        targetedShareIntents.toArray(new Parcelable[targetedShareIntents.size()]));
    }

    private Intent getPromoIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "Test share");
        return intent;
    }

}
