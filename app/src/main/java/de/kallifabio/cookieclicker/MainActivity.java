package de.kallifabio.cookieclicker;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvPoints;

    private int points = 0;
    private int cps = 100;

    private CookieCounter cookieCounter = new CookieCounter();

    private Typeface ttf;

    private Random random;

    private int[] Images = {R.drawable.cursor};
    private String[] Names = {"Clicker"};
    private String[] Description = {"+10 Cookies pro Sekunde"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvPoints = findViewById(R.id.tvPoints);
        ttf = Typeface.createFromAsset(getAssets(), "JandaManateeSolid.ttf");
        tvPoints.setTypeface(ttf);
        random = new Random();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.imgCookie) {
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.cookie_animation);
            animation.setAnimationListener(new SimpleAnimationListener() {
                @Override
                public void onAnimationEnd(Animation animationend) {
                    cookieClick();
                }
            });
            view.startAnimation(animation);
        }
    }

    private void cookieClick() {
        points++;
        tvPoints.setText(Integer.toString(points));
        showToast(R.string.clicked);
    }

    private void showToast(int stringID) {
        final Toast toast = new Toast(this);
        toast.setGravity(Gravity.CENTER | Gravity.LEFT, random.nextInt(600) + 100, random.nextInt(600) - 300);
        toast.setDuration(Toast.LENGTH_SHORT);

        TextView textView = new TextView(this);
        textView.setText(stringID);
        textView.setTextSize(40f);
        textView.setTextColor(Color.DKGRAY);
        textView.setTypeface(ttf);

        toast.setView(textView);

        CountDownTimer toastCountDown;
        toastCountDown = new CountDownTimer(500, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                toast.show();
            }

            @Override
            public void onFinish() {
                toast.cancel();
            }
        };

        toast.show();
        toastCountDown.start();
    }

    private void update() {
        points += cps / 100;
        tvPoints.setText(Integer.toString(points));
    }

    private void showShopFragment() {
        
    }

    public class CookieCounter {

        private Timer timer;

        public CookieCounter() {
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(MainActivity.this::update);
                }
            }, 1000, 10);
        }
    }

    public class ShopAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return Images.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            convertView = getLayoutInflater().inflate(R.layout.item_listview, null);

            ((ImageView) convertView.findViewById(R.id.imgItem)).setImageResource(Images[position]);
            ((TextView) convertView.findViewById(R.id.tvName)).setText(Names[position]);
            ((TextView) convertView.findViewById(R.id.tvDescription)).setText(Description[position]);
            return convertView;
        }
    }
}