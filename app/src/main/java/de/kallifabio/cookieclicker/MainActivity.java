package de.kallifabio.cookieclicker;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvPoints;
    private TextView tvCps;

    private int points;
    private int cps;

    private CookieCounter cookieCounter = new CookieCounter();

    private Typeface ttf;

    private Random random;

    private int[] Images = {R.drawable.cursor};
    private String[] Names = {"Clicker"};
    private String[] Description = {"+100 Cookies pro Sekunde"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvPoints = findViewById(R.id.tvPoints);
        ttf = Typeface.createFromAsset(getAssets(), "JandaManateeSolid.ttf");
        tvPoints.setTypeface(ttf);
        tvCps = findViewById(R.id.tvCps);
        tvCps.setTypeface(ttf);
        random = new Random();
        open();
    }

    private void showCookieFragment() {
        ViewGroup container = findViewById(R.id.container);
        container.removeAllViews();
        container.addView(getLayoutInflater().inflate(R.layout.activity_main, null));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showCookieFragment();
            tvPoints = findViewById(R.id.tvPoints);
            ttf = Typeface.createFromAsset(getAssets(), "JandaManateeSolid.ttf");
            tvPoints.setTypeface(ttf);
            tvCps = findViewById(R.id.tvCps);
            tvCps.setTypeface(ttf);
            random = new Random();
            open();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onPause() {
        super.onPause();
        save();
    }

    private void cookieClick() {
        points++;
        tvPoints.setText(Integer.toString(points));
        showToast(R.string.clicked);
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
        } else if (view.getId() == R.id.btnShop) {
            showShopFragment();
            save();
        }
    }

    private void showToast(int stringID) {
        final Toast toast = new Toast(this);
        toast.setGravity(Gravity.CENTER | Gravity.LEFT, random.nextInt(600) + 100, random.nextInt(600) - 300);
        toast.setDuration(Toast.LENGTH_SHORT);

        TextView textView = new TextView(this);
        textView.setText(stringID);
        textView.setTextSize(40f);
        textView.setTextColor(Color.BLACK);
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
        tvCps.setText(Integer.toString(cps) + " CPS");
    }

    private void save() {
        SharedPreferences preferences = getSharedPreferences("GAME", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("cps", cps);
        editor.putInt("cookies", points);
        editor.apply();
    }

    private void open() {
        SharedPreferences preferences = getSharedPreferences("GAME", 0);
        cps = preferences.getInt("cps", 0);
        points = preferences.getInt("cookies", 0);
    }

    private void showShopFragment() {
        ViewGroup container = findViewById(R.id.container);
        ShopAdapter shopAdapter = new ShopAdapter();
        container.removeAllViews();
        container.addView(getLayoutInflater().inflate(R.layout.activity_shop, null));
        ((ListView) findViewById(R.id.listShop)).setAdapter(shopAdapter);
    }

    private void updateCps(int i) {
        cps += i;
    }

    private void updatePoints(int i) {
        points -= i;
    }

    public class CookieCounter {

        private Timer timer;

        public CookieCounter() {
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(() -> update());
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

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getCount() == 1) {
                        if (points >= 100) {
                            updateCps(100);
                            updatePoints(100);
                            save();
                        } else {
                            (new AlertDialog.Builder(MainActivity.this)).setMessage("Du hast zu wenig Cookies").show();
                        }
                    }
                }
            });

            return convertView;
        }
    }
}