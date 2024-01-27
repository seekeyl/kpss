package com.seekey.kpss.run;

import com.seekey.kpss.panels.MainPanel;
import com.seekey.kpss.helper.DataHelper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;

@Component
@Slf4j
public class Startup {

    private static final int WINDOW_WIDTH = 700;
    private static final int WINDOW_HEIGHT = 380;
    private final DataHelper dataHelper;

    @Autowired
    Startup(DataHelper dataHelper) {
        this.dataHelper = dataHelper;

    }

    @PostConstruct
    public void init() {
        dataHelper.init();
        JFrame frame = new JFrame();
        MainPanel main = new MainPanel(dataHelper);
        frame.setTitle("KPSS");
        frame.setContentPane(main.panel);
        frame.setVisible(true);
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setLocation(20, 20);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
