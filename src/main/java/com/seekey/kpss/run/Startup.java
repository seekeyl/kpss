package com.seekey.kpss.run;

import com.seekey.kpss.panels.MainPanel;
import com.seekey.kpss.helper.DataHelper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@Component
@Slf4j
public class Startup {

    private static final int WINDOW_WIDTH = 700;
    private static final int WINDOW_HEIGHT = 325;
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
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setSystemTray(frame);
    }

    /**
     * 设置系统托盘
     *
     * @param frame 主窗口
     */
    private void setSystemTray(JFrame frame) {
        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().getImage("./images/icon.jpg");
            System.out.println(image);
            TrayIcon trayIcon = setPopup(frame, image);
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                log.error("Error while adding tray icon", e);
            }

            trayIcon.displayMessage("KPSS", "KPSS正在运行", TrayIcon.MessageType.INFO);
            trayIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        frame.setVisible(true);
                        frame.setState(Frame.NORMAL);
                    }
                }
            });
        }
    }

    /**
     * 设置托盘弹出菜单
     *
     * @param frame 主窗口
     * @param image 图标
     * @return 托盘图标
     */
    private static TrayIcon setPopup(JFrame frame, Image image) {
        PopupMenu popup = new PopupMenu();
        MenuItem openItem = new MenuItem("Open KPSS");
        openItem.addActionListener(e -> {
            frame.setVisible(true);
            frame.setState(Frame.NORMAL);
        });
        popup.add(openItem);
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        popup.add(exitItem);

        TrayIcon trayIcon = new TrayIcon(image, "KPSS", popup);
        trayIcon.setImageAutoSize(true);
        return trayIcon;
    }
}
