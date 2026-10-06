package com.nox.urnasimulador;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private static class Stage {
        String cargo;
        int digits;
        Stage(String cargo, int digits) { this.cargo = cargo; this.digits = digits; }
    }

    private final Stage[] stages = {
            new Stage("DEPUTADO FEDERAL", 4),
            new Stage("DEPUTADO ESTADUAL", 5),
            new Stage("SENADOR — 1ª VAGA", 3),
            new Stage("SENADOR — 2ª VAGA", 3),
            new Stage("GOVERNADOR", 2),
            new Stage("PRESIDENTE", 2)
    };

    private int stageIndex = 0;
    private String typed = "";
    private boolean branco = false;
    private final List<String> senateVotes = new ArrayList<>();

    private TextView cargoView;
    private TextView digitsView;
    private TextView statusView;
    private TextView counterView;
    private ToneGenerator tone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tone = new ToneGenerator(AudioManager.STREAM_MUSIC, 45);
        setContentView(buildUi());
        render();
    }

    private View buildUi() {
        ScrollView scroller = new ScrollView(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(12), dp(12), dp(12), dp(16));
        root.setBackgroundColor(Color.rgb(205, 200, 190));
        scroller.addView(root);

        TextView warn = text("SIMULADOR EDUCACIONAL — NÃO OFICIAL\nNÃO REALIZA VOTOS REAIS", 15, true);
        warn.setTextColor(Color.WHITE);
        warn.setGravity(Gravity.CENTER);
        warn.setPadding(dp(8), dp(10), dp(8), dp(10));
        warn.setBackgroundColor(Color.rgb(20, 20, 20));
        root.addView(warn, new LinearLayout.LayoutParams(-1, -2));

        LinearLayout screen = new LinearLayout(this);
        screen.setOrientation(LinearLayout.VERTICAL);
        screen.setPadding(dp(20), dp(22), dp(20), dp(22));
        screen.setBackgroundColor(Color.rgb(245, 246, 241));
        LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(-1, dp(330));
        sp.setMargins(0, dp(12), 0, dp(12));
        root.addView(screen, sp);

        TextView label = text("SEU VOTO PARA", 16, false);
        screen.addView(label);

        cargoView = text("", 28, true);
        cargoView.setPadding(0, dp(8), 0, dp(22));
        screen.addView(cargoView);

        TextView numLabel = text("Número:", 16, false);
        screen.addView(numLabel);

        digitsView = text("", 34, true);
        digitsView.setLetterSpacing(0.18f);
        digitsView.setPadding(0, dp(8), 0, dp(10));
        screen.addView(digitsView);

        statusView = text("", 21, true);
        statusView.setPadding(0, dp(18), 0, 0);
        screen.addView(statusView);

        counterView = text("", 13, false);
        counterView.setTextColor(Color.DKGRAY);
        counterView.setPadding(0, dp(16), 0, 0);
        screen.addView(counterView);

        GridLayout keypad = new GridLayout(this);
        keypad.setColumnCount(3);
        keypad.setRowCount(4);
        keypad.setUseDefaultMargins(true);
        root.addView(keypad, new LinearLayout.LayoutParams(-1, -2));

        for (int n = 1; n <= 9; n++) addNumberButton(keypad, String.valueOf(n), n - 1);
        addSpacer(keypad, 9);
        addNumberButton(keypad, "0", 10);
        addSpacer(keypad, 11);

        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);
        root.addView(actions, new LinearLayout.LayoutParams(-1, -2));

        Button brancoBtn = actionButton("BRANCO", Color.WHITE, Color.BLACK);
        brancoBtn.setOnClickListener(v -> {
            if (stageIndex >= stages.length) return;
            typed = "";
            branco = true;
            beep(ToneGenerator.TONE_DTMF_5, 70);
            render();
        });

        Button corrigeBtn = actionButton("CORRIGE", Color.rgb(224, 112, 26), Color.BLACK);
        corrigeBtn.setOnClickListener(v -> {
            if (stageIndex >= stages.length) {
                resetAll();
                return;
            }
            typed = "";
            branco = false;
            beep(ToneGenerator.TONE_DTMF_2, 80);
            render();
        });

        Button confirmaBtn = actionButton("CONFIRMA", Color.rgb(45, 166, 78), Color.BLACK);
        confirmaBtn.setOnClickListener(v -> confirmVote());

        LinearLayout.LayoutParams ap = new LinearLayout.LayoutParams(0, dp(68), 1f);
        ap.setMargins(dp(3), dp(8), dp(3), 0);
        actions.addView(brancoBtn, ap);
        actions.addView(corrigeBtn, ap);

        LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(0, dp(78), 1.25f);
        cp.setMargins(dp(3), 0, dp(3), 0);
        actions.addView(confirmaBtn, cp);

        TextView foot = text("Treino inspirado no fluxo de votação brasileiro de 2026. Os nomes e números usados no treino não representam candidatos reais.", 12, false);
        foot.setGravity(Gravity.CENTER);
        foot.setTextColor(Color.DKGRAY);
        foot.setPadding(dp(4), dp(12), dp(4), 0);
        root.addView(foot);

        return scroller;
    }

    private void addNumberButton(GridLayout grid, String value, int index) {
        Button b = new Button(this);
        b.setText(value);
        b.setTextSize(26);
        b.setTypeface(Typeface.DEFAULT_BOLD);
        b.setTextColor(Color.WHITE);
        b.setBackgroundColor(Color.rgb(32, 32, 32));
        b.setOnClickListener(v -> pressNumber(value));

        GridLayout.LayoutParams p = new GridLayout.LayoutParams();
        p.width = 0;
        p.height = dp(60);
        p.columnSpec = GridLayout.spec(index % 3, 1f);
        p.rowSpec = GridLayout.spec(index / 3);
        p.setMargins(dp(4), dp(4), dp(4), dp(4));
        grid.addView(b, p);
    }

    private void addSpacer(GridLayout grid, int index) {
        View v = new View(this);
        GridLayout.LayoutParams p = new GridLayout.LayoutParams();
        p.width = 0;
        p.height = dp(60);
        p.columnSpec = GridLayout.spec(index % 3, 1f);
        p.rowSpec = GridLayout.spec(index / 3);
        grid.addView(v, p);
    }

    private Button actionButton(String label, int bg, int fg) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextSize(13);
        b.setTypeface(Typeface.DEFAULT_BOLD);
        b.setTextColor(fg);
        b.setBackgroundColor(bg);
        return b;
    }

    private TextView text(String s, int size, boolean bold) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextSize(size);
        t.setTextColor(Color.BLACK);
        if (bold) t.setTypeface(Typeface.DEFAULT_BOLD);
        return t;
    }

    private void pressNumber(String n) {
        if (stageIndex >= stages.length || branco) return;
        int max = stages[stageIndex].digits;
        if (typed.length() < max) {
            typed += n;
            beep(ToneGenerator.TONE_DTMF_1, 50);
            render();
        }
    }

    private void confirmVote() {
        if (stageIndex >= stages.length) return;
        Stage s = stages[stageIndex];

        if (!branco && typed.length() != s.digits) {
            statusView.setText("DIGITE TODOS OS DÍGITOS");
            beep(ToneGenerator.TONE_SUP_ERROR, 180);
            return;
        }

        if (stageIndex == 3 && !branco && senateVotes.size() == 1 && typed.equals(senateVotes.get(0))) {
            statusView.setText("ESCOLHA OUTRO CANDIDATO\nA 2ª vaga do Senado deve ser diferente.");
            beep(ToneGenerator.TONE_SUP_ERROR, 220);
            return;
        }

        if (stageIndex == 2 && !branco) senateVotes.add(typed);

        beep(ToneGenerator.TONE_PROP_ACK, 140);
        stageIndex++;
        typed = "";
        branco = false;
        render();
    }

    private void render() {
        if (stageIndex >= stages.length) {
            cargoView.setText("FIM");
            cargoView.setTextSize(56);
            cargoView.setGravity(Gravity.CENTER);
            digitsView.setText("");
            statusView.setText("TREINO CONCLUÍDO\nNenhum voto real foi registrado.");
            statusView.setGravity(Gravity.CENTER);
            counterView.setText("Pressione CORRIGE para reiniciar.");
            return;
        }

        cargoView.setTextSize(28);
        cargoView.setGravity(Gravity.START);
        Stage s = stages[stageIndex];
        cargoView.setText(s.cargo);

        StringBuilder boxes = new StringBuilder();
        for (int i = 0; i < s.digits; i++) {
            if (i < typed.length()) boxes.append(typed.charAt(i));
            else boxes.append("□");
            if (i < s.digits - 1) boxes.append(" ");
        }
        digitsView.setText(boxes.toString());

        if (branco) {
            statusView.setText("VOTO EM BRANCO\nPressione CONFIRMA.");
        } else if (typed.length() == s.digits) {
            statusView.setText("NÚMERO DIGITADO: " + typed + "\nTreino — sem candidato real.");
        } else {
            statusView.setText("Digite o número para este cargo.");
        }

        counterView.setText("Etapa " + (stageIndex + 1) + " de " + stages.length + "  •  BRANCO / CORRIGE / CONFIRMA");
    }

    private void resetAll() {
        stageIndex = 0;
        typed = "";
        branco = false;
        senateVotes.clear();
        render();
    }

    private void beep(int toneType, int ms) {
        if (tone != null) tone.startTone(toneType, ms);
    }

    private int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tone != null) {
            tone.release();
            tone = null;
        }
    }
}
