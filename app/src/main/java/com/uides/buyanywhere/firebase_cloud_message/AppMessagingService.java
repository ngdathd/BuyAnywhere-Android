package com.uides.buyanywhere.firebase_cloud_message;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.R;
import com.uides.buyanywhere.custom_view.PriceTextView;
import com.uides.buyanywhere.ui.activity.ProductDetailLoadingActivity;
import com.uides.buyanywhere.ui.activity.ShopActivity;
import com.uides.buyanywhere.utils.SharedPreferencesOpenHelper;
import com.uides.buyanywhere.view_pager_adapter.ShopPagerAdapter;

import java.io.IOException;
import java.util.Map;


/**
 * Created by TranThanhTung on 26/09/2017.
 */

public class AppMessagingService extends FirebaseMessagingService {
    private static final String TAG = "AppMessagingService";

    public static final int NEW_ORDER_NOTIFICATION = 0;
    public static final int NEW_PRODUCT_NOTIFICATION = 1;

    public static final String TYPE = "type";
    public static final String ORDER_COUNT = "orderCount";
    private static final String PRODUCT_ID = "id";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        resolveNotification(remoteMessage);
    }

    public void resolveNotification(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        String type = data.get(TYPE);
        int id = Integer.parseInt(type);
        switch (id) {
            case NEW_ORDER_NOTIFICATION: {
                int orderCount = Integer.parseInt(data.get(ORDER_COUNT));
                Notification notification = buildNewOrderNotification(orderCount);

                if (notification != null) {
                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.notify(id, notification);
                }
            }
            break;

            case NEW_PRODUCT_NOTIFICATION: {
                new LoadImageService(data, this).execute();
            }
            break;

            default: {
                break;
            }
        }
    }

    public Notification buildNewOrderNotification(int orderCount) {
        String shopID = SharedPreferencesOpenHelper.getShopID(this);
        if (shopID == null) {
            return null;
        }
        Intent intent = new Intent(this, ShopActivity.class);
        intent.putExtra(Constant.SHOP_ID, shopID);
        intent.putExtra(Constant.ACTIVE_TAB, ShopPagerAdapter.SHOP_ORDER_FRAGMENT_INDEX);
        PendingIntent pIntent = PendingIntent.getActivity(
                this,
                1,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.item_shop_order_notification);
        remoteViews.setTextViewText(R.id.txt_content, "Có " + orderCount + " yêu cầu đặt hàng mới");

        return new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_logo)
                .setContentIntent(pIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true)
                .setContent(remoteViews).build();
    }

    public static Notification buildNewProductNotification(Context context, Map<String, String> data, Bitmap bitmap) {
        Intent intent = new Intent(context, ProductDetailLoadingActivity.class);
        intent.putExtra(Constant.PRODUCT_ID, data.get(PRODUCT_ID));
        PendingIntent pIntent = PendingIntent.getActivity(
                context,
                1,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.item_product_notification);
        if (bitmap == null) {
            remoteViews.setImageViewResource(R.id.img_preview, R.drawable.ic_logo);
        } else {
            remoteViews.setImageViewBitmap(R.id.img_preview, bitmap);
        }
        remoteViews.setTextViewText(R.id.txt_name, data.get("name"));
        remoteViews.setTextViewText(R.id.txt_shop_name, data.get("shopName"));
        String rawCurrentPrice = data.get("currentPrice");
        double cPrice = Double.parseDouble(rawCurrentPrice);
        String currentPrice = PriceTextView.reformatPrice("" + ((int) cPrice), "đ");
        remoteViews.setTextViewText(R.id.txt_current_price, currentPrice);
        String rawOriginPrice = data.get("originPrice");
        if (!rawCurrentPrice.equals(rawOriginPrice)) {
            double oPrice = Double.parseDouble(rawOriginPrice);
            String originPrice = PriceTextView.reformatPrice("" + ((int) oPrice), "đ");
            remoteViews.setTextViewText(R.id.txt_origin_price, originPrice);
        }
        remoteViews.setTextViewText(R.id.txt_time, data.get("createdDate"));

        return new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_logo)
                .setContentIntent(pIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true)
                .setContent(remoteViews).build();
    }

    private static class LoadImageService extends AsyncTask<Void, Void, Bitmap> {
        Map<String, String> data;
        Context context;

        public LoadImageService(Map<String, String> data, Context context) {
            this.data = data;
            this.context = context;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            try {
                return Picasso.with(context).load(data.get("previewUrl")).get();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            Notification notification = buildNewProductNotification(context, data, bitmap);
            if (notification != null) {
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(NEW_PRODUCT_NOTIFICATION, notification);
            }
        }
    }
}
