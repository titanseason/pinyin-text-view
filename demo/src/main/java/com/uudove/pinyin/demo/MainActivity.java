package com.uudove.pinyin.demo;

import java.util.ArrayList;
import java.util.List;

import com.uudove.pinyin.widget.PinyinTextView;

import android.app.Activity;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;

public class MainActivity extends Activity implements View.OnClickListener {

    private PinyinTextView mPinyinTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPinyinTextView = (PinyinTextView) findViewById(R.id.pinyin_text_view);

        findViewById(R.id.pinyin_text_btn).setOnClickListener(this);
        findViewById(R.id.plain_text_btn).setOnClickListener(this);

        mPinyinTextView.setDebugDraw(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pinyin_text_btn:
                showPinyinAndText();
                break;

            case R.id.plain_text_btn:
                showPlainText();
                break;
        }
    }

    private void showPinyinAndText() {
        List<Pair<String, String>> pairList = new ArrayList<Pair<String, String>>();

        pairList.add(Pair.create("你", "nǐ"));
        pairList.add(Pair.create("在", "zài"));
        pairList.add(Pair.create("哪", "nǎ"));
        pairList.add(Pair.create("？", " "));
        pairList.add(Pair.create("我", "wǒ"));
        pairList.add(Pair.create("在", "zài"));
        pairList.add(Pair.create("家", "jiā"));
        pairList.add(Pair.create("，", " "));
        pairList.add(Pair.create("很", "hěn"));
        pairList.add(Pair.create("高", "gāo"));
        pairList.add(Pair.create("兴", "xìng"));
        pairList.add(Pair.create("认", "rèn"));
        pairList.add(Pair.create("识", "shi"));
        pairList.add(Pair.create("你", "nǐ"));
        pairList.add(Pair.create("！", " "));
        pairList.add(Pair.create("你", "nǐ"));
        pairList.add(Pair.create("在", "zài"));
        pairList.add(Pair.create("哪", "nǎ"));
        pairList.add(Pair.create("？", " "));
        pairList.add(Pair.create("我", "wǒ"));
        pairList.add(Pair.create("在", "zài"));
        pairList.add(Pair.create("家", "jiā"));
        pairList.add(Pair.create("，", " "));
        pairList.add(Pair.create("很", "hěn"));
        pairList.add(Pair.create("高", "gāo"));
        pairList.add(Pair.create("兴", "xìng"));
        pairList.add(Pair.create("认", "rèn"));
        pairList.add(Pair.create("识", "shi"));
        pairList.add(Pair.create("你", "nǐ"));
        pairList.add(Pair.create("！", " "));
        pairList.add(Pair.create("你", "nǐ"));
        pairList.add(Pair.create("在", "zài"));
        pairList.add(Pair.create("哪", "nǎ"));
        pairList.add(Pair.create("？", " "));
        pairList.add(Pair.create("我", "wǒ"));
        pairList.add(Pair.create("在", "zài"));
        pairList.add(Pair.create("家", "jiā"));
        pairList.add(Pair.create("，", " "));
        pairList.add(Pair.create("很", "hěn"));
        pairList.add(Pair.create("高", "gāo"));
        pairList.add(Pair.create("兴", "xìng"));
        pairList.add(Pair.create("认", "rèn"));
        pairList.add(Pair.create("识", "shi"));
        pairList.add(Pair.create("你", "nǐ"));
        pairList.add(Pair.create("！", " "));
        pairList.add(Pair.create("你", "nǐ"));
        pairList.add(Pair.create("在", "zài"));
        pairList.add(Pair.create("哪", "nǎ"));
        pairList.add(Pair.create("？", " "));
        pairList.add(Pair.create("我", "wǒ"));
        pairList.add(Pair.create("在", "zài"));
        pairList.add(Pair.create("家", "jiā"));
        pairList.add(Pair.create("，", " "));
        pairList.add(Pair.create("很", "hěn"));
        pairList.add(Pair.create("高", "gāo"));
        pairList.add(Pair.create("兴", "xìng"));
        pairList.add(Pair.create("认", "rèn"));
        pairList.add(Pair.create("识", "shi"));
        pairList.add(Pair.create("你", "nǐ"));
        pairList.add(Pair.create("！", " "));

        mPinyinTextView.setPinyinText(pairList);
    }

    private void showPlainText() {
        String text = "很高兴认识你！Nice to meet you! 你好吗？How are you? 我很好。I'm fine, thank you.";
        mPinyinTextView.setText(text);
    }
}
