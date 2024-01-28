package com.seekey.kpss.panels;

import cn.hutool.crypto.digest.DigestUtil;
import com.seekey.kpss.commons.Sm2Util;
import com.seekey.kpss.entity.Group;
import com.seekey.kpss.entity.Key;
import com.seekey.kpss.entity.Secret;
import com.seekey.kpss.helper.DataHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

public class MainPanel {
    private JButton btn1;
    public JPanel panel;
    private JTable table;
    private JList<Object> list1;
    private JTextField nameBox;
    private JTextField userBox;
    private JPasswordField passwordBox;
    private JTextField urlBox;
    private JButton uptbtn;
    private JTextArea remarkBox;
    private JComboBox<String> groupBox;
    private JLabel tips;
    private JToolBar toolbarBox;
    private JButton btn0;
    private JButton btn2;
    private JButton prePage;
    private JButton nxtPage;
    private JLabel pageLbl;
    private Integer keyId;
    private final DataHelper dataHelper;
    private List<Group> groups;
    private List<Key> keys;
    private String oldPwd;
    private String publicKey;
    private String privateKey;
    private Integer pageNo = 1;
    private final Integer pageSize = 10;

    public MainPanel(DataHelper dataHelper) {
        this.dataHelper = dataHelper;

        uptbtn.addActionListener(e -> saveKey());

        list1.addListSelectionListener(e -> {
            int index = list1.getSelectedIndex();
            if (index < 0 || index >= groups.size()) {
                return;
            }
            // Group group = groups.get(index);
            pageNo = 1;
            refreshKeys();
            setPage();
            // System.out.println(JSON.toJSONString(group));
        });

        list1.addMouseListener(new textListener());
        table.addMouseListener(new textListener());

        btn0.addActionListener(e -> changePassword());

        btn1.addActionListener(e -> addGroup());

        btn2.addActionListener(e -> setAddKey());

        prePage.addActionListener(e -> {
            if (pageNo > 1) {
                pageNo--;
                refreshKeys();
            }
            setPage();
        });

        nxtPage.addActionListener(e -> {
            if (keys.size() == pageSize) {
                pageNo++;
                refreshKeys();
            }
            setPage();
        });

        nameBox.setName("名称");
        userBox.setName("用户");
        passwordBox.setName("密码");
        urlBox.setName("地址");
        remarkBox.setName("描述");

        nameBox.addMouseListener(new textListener());
        urlBox.addMouseListener(new textListener());
        userBox.addMouseListener(new textListener());
        passwordBox.addMouseListener(new textListener());
        remarkBox.addMouseListener(new textListener());

        getSecret();
        refreshGroups();
        refreshKeys();

        setPage();
    }

    /**
     * 修改读取密码
     */
    private void changePassword() {
        JOptionPane pane = new JOptionPane("修改密码", JOptionPane.INFORMATION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
        Box hbox0 = Box.createHorizontalBox();
        JLabel cpassLabel = new JLabel("当前密码");
        hbox0.add(cpassLabel);
        JPasswordField cpassBox = new JPasswordField();
        hbox0.add(cpassBox);
        pane.add(hbox0, 1);

        Box hbox1 = Box.createHorizontalBox();
        JLabel npassLabel = new JLabel("新的密码");
        hbox1.add(npassLabel);
        JPasswordField npassBox = new JPasswordField();
        hbox1.add(npassBox);
        pane.add(hbox1, 2);

        Box hbox2 = Box.createHorizontalBox();
        JLabel fpassLabel = new JLabel("确认密码");
        hbox2.add(fpassLabel);
        JPasswordField fpassBox = new JPasswordField();
        hbox2.add(fpassBox);
        pane.add(hbox2, 3);

        JDialog dialog = pane.createDialog("密码校验");
        dialog.setVisible(true);

        switch ((Integer) pane.getValue()) {
            case JOptionPane.OK_OPTION:
                String cpass = new String(cpassBox.getPassword());
                String npass = new String(npassBox.getPassword());
                String fpass = new String(fpassBox.getPassword());
                Secret secret = dataHelper.getSecret();
                if (!DigestUtil.md5Hex(cpass).equals(secret.getPassword())) {
                    JOptionPane.showMessageDialog(panel, "当前密码错误");
                    return;
                }
                if (!npass.equals(fpass)) {
                    JOptionPane.showMessageDialog(panel, "两次密码不一致");
                    return;
                }
                dataHelper.updatePassword(npass);
                JOptionPane.showMessageDialog(panel, "密码修改成功");
                break;
            case JOptionPane.CANCEL_OPTION:
                break;
        }
        dialog.dispose();
    }

    /**
     * 设置页码与翻页按钮状态
     */
    private void setPage() {
        prePage.setEnabled(pageNo > 1);
        nxtPage.setEnabled(keys.size() >= pageSize);
        pageLbl.setText("第" + pageNo + "页");
    }

    /**
     * 复制密码到剪贴板
     */
    private void copyPassword() {
        Secret secret = dataHelper.getSecret();
        if (!DigestUtil.md5Hex("").equalsIgnoreCase(secret.getPassword())) {
            JPasswordField passBox = new JPasswordField();
            JOptionPane pane = new JOptionPane("输入密码", JOptionPane.QUESTION_MESSAGE);
            pane.add(passBox, 1);
            JDialog dialog = pane.createDialog("密码校验");
            dialog.setVisible(true);
            String pass = new String(passBox.getPassword());
            dialog.dispose();

            String pmd5 = DigestUtil.md5Hex(pass);
            if (!pmd5.equals(secret.getPassword())) {
                JOptionPane.showMessageDialog(panel, "密码错误");
                return;
            }
        }

        String pwd = new String(passwordBox.getPassword());
        copy(Sm2Util.decrypt(privateKey, pwd));
        tips.setText("密码已复制");
    }

    /**
     * 复制文本到剪贴板
     * @param text 文本
     */
    private void copy(String text) {
        StringSelection selection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, null);
    }

    /**
     * 获取加密信息
     */
    private void getSecret() {
        Secret secret = dataHelper.getSecret();
        if (secret == null) {
            return;
        }
        publicKey = secret.getPublicKey();
        privateKey = secret.getPrivateKey();
    }

    /**
     * 保存密钥
     */
    private void saveKey() {
        Group group = groups.get(groupBox.getSelectedIndex());

        String pwd = new String(passwordBox.getPassword());
        if (!pwd.equals(oldPwd)) {
            pwd = Sm2Util.encrypt(publicKey, pwd);
        }

        Key key = new Key();
        key.setId(keyId);
        key.setGroupId(group.getId());
        key.setKeyName(nameBox.getText());
        key.setUserName(userBox.getText());
        key.setPassword(pwd);
        key.setUrl(urlBox.getText());
        key.setRemark(remarkBox.getText());

        passwordBox.setText(pwd);
        dataHelper.modifyKey(key);
        refreshKeys();

        JOptionPane.showMessageDialog(panel, "保存成功");
    }

    /**
     * 复制密钥信息到界面
     */
    private void setKey() {
        Key key = keys.get(table.getSelectedRow());
        keyId = key.getId();
        groupBox.setSelectedIndex(list1.getSelectedIndex());
        nameBox.setText(key.getKeyName());
        userBox.setText(key.getUserName());
        passwordBox.setText(key.getPassword());
        urlBox.setText(key.getUrl());
        remarkBox.setText(key.getRemark());
        oldPwd = key.getPassword();
        uptbtn.setEnabled(true);
    }

    /**
     * 刷新密钥列表
     */
    private void refreshKeys() {
        uptbtn.setEnabled(false);
        //创建一维数组，存储标题
        Object[] titles = {"名称","用户","地址"};
        int indexId = Math.max(list1.getSelectedIndex(), 0);
        if (list1.getSelectedIndex() < 0){
            list1.setSelectedIndex(indexId);
        }

        Group group = groups.get(indexId);
        keys = dataHelper.getKeys(group.getId(), pageNo, pageSize);
        if (keys == null || keys.isEmpty()) {
            Object[][] data = new Object[1][];
            table.setModel(new DefaultTableModel(data, titles));
            return;
        }

        //创建二维数组，存储数据
        Object[][] data = new Object[keys.size()][];
        keys.forEach(key -> {
            Object[] obj = new Object[3];
            obj[0] = key.getKeyName();
            obj[1] = key.getUserName();
            obj[2] = key.getUrl();
            data[keys.indexOf(key)] = obj;
        });
        table.setModel(new DefaultTableModel(data, titles));

        groupBox.setSelectedIndex(list1.getSelectedIndex());
    }

    /**
     * 刷新分组列表
     */
    private void refreshGroups() {
        groups = dataHelper.getGroups();
        groupBox.removeAllItems();
        Object[] names = new Object[groups.size()];
        for (int i = 0; i < groups.size(); i++) {
            names[i] = groups.get(i).getName();
            groupBox.addItem(groups.get(i).getName());
        }
        list1.setListData(names);
    }

    /**
     * 添加密钥右键菜单
     * @param x x坐标
     * @param y y坐标
     */
    private void addKeyMenu(int x, int y) {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem item0 = new JMenuItem("新增");
        item0.addActionListener(e -> setAddKey());
        popupMenu.add(item0);

        JMenuItem item1 = new JMenuItem("删除");
        item1.addActionListener(e -> deleteKey());
        popupMenu.add(item1);
        popupMenu.show(table, x, y);
    }

    /**
     * 添加分组右键菜单
     * @param x x坐标
     * @param y y坐标
     */
    private void addGroupMenu(int x, int y) {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem item0 = new JMenuItem("新增");
        item0.addActionListener(e -> addGroup());
        popupMenu.add(item0);

        JMenuItem item1 = new JMenuItem("删除");
        item1.addActionListener(e -> deleteGroup());
        popupMenu.add(item1);
        popupMenu.show(list1, x, y);
    }

    /**
     * 设置新增密钥
     */
    private void setAddKey() {
        keyId = null;
        groupBox.setSelectedIndex(list1.getSelectedIndex());
        nameBox.setText("");
        userBox.setText("");
        passwordBox.setText("");
        urlBox.setText("");
        remarkBox.setText("");

        oldPwd = "";
        uptbtn.setEnabled(true);
    }

    /**
     * 删除密钥
     */
    private void deleteKey() {
        int confirm = JOptionPane.showConfirmDialog(panel, "是否确认删除");
        if (confirm != 0) {
            return;
        }

        Key key = keys.get(table.getSelectedRow());
        dataHelper.deleteKeyById(key.getId());
        refreshKeys();
        setAddKey();
    }

    /**
     * 新增分组
     */
    private void addGroup() {
        String name = JOptionPane.showInputDialog("请输入分组名称");
        if (name == null || name.isEmpty()) {
            return;
        }

        Group group = new Group();
        group.setName(name);
        dataHelper.modifyGroup(group);
        refreshGroups();
    }

    /**
     * 删除分组
     */
    private void deleteGroup() {
        int confirm = JOptionPane.showConfirmDialog(panel, "是否确认删除");
        if (confirm != 0) {
            return;
        }

        int index = list1.getSelectedIndex();
        if (index < 0 || index >= groups.size()) {
            return;
        }
        Group group = groups.get(index);
        dataHelper.deleteGroupById(group.getId());
        refreshGroups();
    }

    /**
     * 鼠标事件监听
     */
    class textListener implements MouseListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            Component component = e.getComponent();
            // 如果是右键
            if (e.getButton() == MouseEvent.BUTTON3) {
                // 如果是密码框，复制密码
                if (component instanceof JPasswordField) {
                    copyPassword();
                // 如果是文本框，复制文本
                }else if (component instanceof JTextField) {
                    JTextField textField = (JTextField) component;
                    copy(textField.getText());
                    tips.setText(component.getName() + "已复制");
                // 如果是文本域，复制文本
                }else if (component instanceof JTextArea) {
                    JTextArea textArea = (JTextArea) component;
                    copy(textArea.getText());
                    tips.setText(component.getName() + "已复制");
                // 如果是密钥列表，添加密钥菜单
                }else if (component instanceof JTable) {
                    addKeyMenu(e.getX(), e.getY());
                // 如果是分组列表，添加分组菜单
                } else if (component instanceof JList) {
                    addGroupMenu(e.getX(), e.getY());
                }
            }
            // 如果是左键
            if (e.getButton() == MouseEvent.BUTTON1) {
                // 如果是密钥列表，设置密钥信息
                if (component instanceof JTable){
                    int row = table.getSelectedRow();
                    if (keys != null && row < keys.size()) {
                        setKey();
                    }
                // 如果是文本框，全选文本
                }else if (component instanceof JTextField) {
                    tips.setText(component.getName() + "已选中");
                    ((JTextField) component).selectAll();
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }
}
