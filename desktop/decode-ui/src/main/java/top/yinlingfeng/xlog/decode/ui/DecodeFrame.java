package top.yinlingfeng.xlog.decode.ui;

import YXLogDecode.desktop.decode_ui.BuildConfig;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatSVGUtils;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.ini4j.Wini;
import top.yinlingfeng.xlog.decode.core.XLogFileDecode;
import top.yinlingfeng.xlog.decode.core.log.LogInfo;
import top.yinlingfeng.xlog.decode.core.log.LogLevel;
import top.yinlingfeng.xlog.decode.core.log.LogUtil;
import top.yinlingfeng.xlog.decode.ui.bean.*;
import top.yinlingfeng.xlog.decode.ui.util.FontAttribute;
import top.yinlingfeng.xlog.decode.ui.util.Utils;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import static top.yinlingfeng.xlog.decode.core.log.LogUtil.LOGGER;


public class DecodeFrame extends JFrame {

    //设置公共字体
    private Font commonFont;
    //主Pane
    private JPanel contentPane;
    //布局类型
    private MigLayout layout;
    //日志路径输入框
    private JTextField logPathTextField;
    //选择日志路径按钮
    private JButton btnSelectLogPathButton;
    //日志解密后保存路径
    private JTextField logDecodeSavePathTextField;
    //选择解密后保存的路径
    private JButton btnSelectLogDecodeSavePathButton;
    //选择流式解压下拉框
    private JComboBox<DecompressionType> selectDecompressionComboBox;
    //选择密钥选择框
    private JCheckBox selectPrivateKeyCheckBox;
    //选择密钥下拉框
    private JComboBox<PrivateKeyInfo> selectPrivateKeyInfoComboBox;
    //删除密钥
    private JButton btnDeletePrivateKeyInfoButton;
    //输入密钥选择框
    private JCheckBox inputPrivateKeyCheckBox;
    //密钥输入框
    private JTextArea inputPrivateKeyTextArea;
    //保存密钥
    private JButton btnSavePrivateKeyInfoButton;
    //解密成功计数
    private JLabel successCountLabel;
    //解密失败计数
    private JLabel failedCountLabel;
    //开始解密按钮
    private JButton btnStartDecodeButton;
    //解密详细信息展示
    private JTextPane decodeInfotextPane;
    //解密信息
    private Document decodeInfoDocs;
    //信息显示的字体设置
    private FontAttribute hintFontAttribute, errorFontAttribute;
    //右键菜单
    private JPopupMenu rightMenu;
    //菜单项-复制
    private JMenuItem copyItem;
    //菜单项-清除
    private JMenuItem clearItem;

    //得到显示器屏幕的宽高
    private int width = Toolkit.getDefaultToolkit().getScreenSize().width;
    private int height = Toolkit.getDefaultToolkit().getScreenSize().height;

    //默认配置文件
    private static final String DEFAULT_INI_FILE = "top/yinlingfeng/xlog/decode/ui/config.ini";
    //实际使用的配置文件
    private static final String TEMP_INI_FILE = "./config.ini";

    private static final String DEFAULT_LOGO = "/top/yinlingfeng/xlog/decode/ui/logo.svg";

    public DecodeFrame() {

        FlatLightLaf.setup();
        //初始化UI
        initView();
        //初始化数据
        initData();
    }

    /**
     * 初始化UI
     */
    private void initView() {
        setAppIconImages();
        setFont();
        setContentPaneView();
        addSelectLogPathView();
        addLogPrivateKeyView();
        //decodeLogDecompressionTypeSelectView();
        decodeLogStatisticsView();
        decodeOperationInfoView();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        //初始化设置
        initSetting();

    }

    private void initSetting() {
        //初始化日志配置
        initLog();
        //初始化配置设置
        initConfigSetting();
        //初始化默认选择
        initSelectSetting();
    }

    private void initLog() {
        LogUtil.setmLogInfo(logInfo);
    }

    private final List<PrivateKeyInfo> privateKeyInfoData = new ArrayList<>();

    private final OperationSave operationSave = new OperationSave();

    private void initConfigSetting() {
        initPrivateKey(0);
        initOperationSave();
    }

    private void initPrivateKey(int selectedIndex) {
        try {
            Wini ini = getWini();
            privateKeyInfoData.clear();
            selectPrivateKeyInfoComboBox.removeAllItems();
            if (ini.get(IniSelectionEnum.APP.getName()) == null) {
                setSelectPrivateKeyModeIsFalse();
                return;
            }
            Set<String> tempSet = ini.get(IniSelectionEnum.APP.getName()).keySet();
            for (String name : tempSet) {
                String privateKey = ini.get(IniSelectionEnum.PRIVATE_KEY.getName(), name, String.class);
                PrivateKeyInfo privateKeyInfo = new PrivateKeyInfo(name, privateKey);
                privateKeyInfoData.add(privateKeyInfo);
                selectPrivateKeyInfoComboBox.addItem(privateKeyInfo);
            }
            if (privateKeyInfoData.size() > 0) {
                if (selectedIndex >= privateKeyInfoData.size()) {
                    selectedIndex = 0;
                }
                //选择密钥选择框
                selectPrivateKeyCheckBox.setEnabled(true);
                selectPrivateKeyInfoComboBox.setSelectedIndex(selectedIndex);
            } else {
                setSelectPrivateKeyModeIsFalse();
            }
        } catch (IOException | URISyntaxException e) {
            LogUtil.ei("异常信息：" + ExceptionUtils.getStackTrace(e));
            setSelectPrivateKeyModeIsFalse();
        }

    }

    private void setSelectPrivateKeyModeIsFalse() {
        //选择密钥选择框
        selectPrivateKeyCheckBox.setEnabled(false);
        //选择密钥下拉框
        selectPrivateKeyInfoComboBox.setEnabled(false);
        //删除密钥
        btnDeletePrivateKeyInfoButton.setEnabled(false);
        inputPrivateKeyCheckBox.setSelected(true);
    }

    private Wini getWini() throws URISyntaxException, IOException {
        File tempIniFile = new File(TEMP_INI_FILE);
        if (!tempIniFile.exists()) {
            boolean result = tempIniFile.createNewFile();
            LogUtil.ei("创建配置文件结果：" + result);
            InputStream inputStream = getClass()
                    .getClassLoader().getResourceAsStream(DEFAULT_INI_FILE);
            FileOutputStream outputStream = new FileOutputStream(tempIniFile);
            IOUtils.copy(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
        }
        return new Wini(tempIniFile);
    }

    private void initOperationSave() {
        try {
            Wini ini = getWini();
            String logFilePath = ini.get(IniSelectionEnum.OPERATION_SAVE.getName(), IniOptionNameEnum.LOG_FILE_PATH.getName(), String.class);
            if (logFilePath != null && !logFilePath.isEmpty()) {
                operationSave.setLogFilePath(logFilePath);
            }
            String saveLogPath = ini.get(IniSelectionEnum.OPERATION_SAVE.getName(), IniOptionNameEnum.SAVE_TO_PATH.getName(), String.class);
            if (saveLogPath != null && !saveLogPath.isEmpty()) {
                operationSave.setSaveLogPath(saveLogPath);
            }
            Integer privateKeySelectMode = ini.get(IniSelectionEnum.OPERATION_SAVE.getName(), IniOptionNameEnum.PRIVATE_KEY_SELECT_MODE.getName(), Integer.class);
            if (privateKeySelectMode != null) {
                operationSave.setPrivateKeySelectMode(privateKeySelectMode);
            }
            Integer selectPrivateKeySelectIndex = ini.get(IniSelectionEnum.OPERATION_SAVE.getName(), IniOptionNameEnum.SELECT_PRIVATE_KEY_SELECT_INDEX.getName(), Integer.class);
            if (selectPrivateKeySelectIndex != null) {
                operationSave.setSelectPrivateKeySelectIndex(selectPrivateKeySelectIndex);
            }
            String inputPrivateKeyInfo = ini.get(IniSelectionEnum.OPERATION_SAVE.getName(), IniOptionNameEnum.INPUT_PRIVATE_KEY_INFO.getName(), String.class);
            if (inputPrivateKeyInfo != null && !inputPrivateKeyInfo.isEmpty()) {
                operationSave.setInputPrivateKeyInfo(inputPrivateKeyInfo);
            }
        } catch (IOException | URISyntaxException e) {
            LogUtil.ei("异常信息：" + ExceptionUtils.getStackTrace(e));
        }

    }

    private void initSelectSetting() {
        if (!operationSave.getLogFilePath().isEmpty()) {
            logPathTextField.setText(operationSave.getLogFilePath());
        }
        if (!operationSave.getSaveLogPath().isEmpty()) {
            logDecodeSavePathTextField.setText(operationSave.getSaveLogPath());
        }
        if (operationSave.getPrivateKeySelectMode() == 1) {
            selectPrivateKeyCheckBox.setSelected(true);
            btnSavePrivateKeyInfoButton.setText("修改");
            inputPrivateKeyTextArea.setEnabled(false);
            if (operationSave.getSelectPrivateKeySelectIndex() >= selectPrivateKeyInfoComboBox.getItemCount()) {
                operationSave.setSelectPrivateKeySelectIndex(0);
            }
            selectPrivateKeyInfoComboBox.setSelectedIndex(operationSave.getSelectPrivateKeySelectIndex());
        } else {
            inputPrivateKeyCheckBox.setSelected(true);
            btnSavePrivateKeyInfoButton.setText("保存");
            inputPrivateKeyTextArea.setEnabled(true);
            if (!operationSave.getInputPrivateKeyInfo().isEmpty()) {
                inputPrivateKeyTextArea.setText(operationSave.getInputPrivateKeyInfo());
            }
        }

    }

    /**
     * 设置程序图标
     */
    private void setAppIconImages() {
        setIconImages( FlatSVGUtils.createWindowIconImages( DEFAULT_LOGO ) );
    }

    /**
     * 设置共用字体
     */
    private void setFont() {
        commonFont = new Font("微软雅黑", Font.PLAIN, 12);
    }

    /**
     * 监听程序
     */
    private WindowListener windowListener = new WindowListener() {

        @Override
        public void windowOpened(WindowEvent e) {
            LogUtil.i("windowOpened");
        }

        @Override
        public void windowIconified(WindowEvent e) {
            LogUtil.i("windowIconified");
        }

        @Override
        public void windowDeiconified(WindowEvent e) {
            LogUtil.i("windowDeiconified");
        }

        @Override
        public void windowDeactivated(WindowEvent e) {
            LogUtil.i("windowDeactivated");
        }

        @Override
        public void windowClosing(WindowEvent e) {
            LogUtil.i("windowClosing");
        }

        @Override
        public void windowClosed(WindowEvent e) {
            LogUtil.i("windowClosed");
        }

        @Override
        public void windowActivated(WindowEvent e) {
            LogUtil.i("windowActivated");
        }
    };

    private final LogInfo logInfo = new LogInfo() {
        @Override
        public void log(LogLevel priority, String tag, String message) {
            if (priority == LogLevel.LEVEL_ERROR) {
                decodeInfoText(message, errorFontAttribute);
                LOGGER.info(message);
            } else if (priority == LogLevel.LEVEL_EXTRA){
                decodeInfoText(message, hintFontAttribute);
                LOGGER.info(message);
            } else {
                LOGGER.info(message);
            }
        }
    };

    private void decodeInfoText(String text, FontAttribute fontAttribute) {
        try {
            decodeInfoDocs.insertString(decodeInfoDocs.getLength(), text + "\n\n", fontAttribute.getAttrSet());
        } catch (BadLocationException e) {
            LogUtil.ei("异常信息：" + ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     *设置主界面整体展示UI
     */
    private void setContentPaneView() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds((width - 480) / 2,
                (height - 550) / 2, 480, 550);
        setResizable(true);
        setFont(commonFont);
        setTitle("XLog解密" + BuildConfig.YXLogDecodeVersion);
        addWindowListener(windowListener);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        layout = new MigLayout("",
                "[pref!]" +
                        "[grow,fill]" +
                        "[pref!]",
                "[][][][][][grow,fill][]");
        contentPane.setLayout(layout);
    }

    /**
     * 增加日志文件地址UI
     */
    private void addSelectLogPathView() {
        //log路径label
        JLabel apkPathLabel = new JLabel("日志路径：");
        apkPathLabel.setFont(commonFont);
        contentPane.add(apkPathLabel);

        //log路径输入框
        logPathTextField = new JTextField();
        logPathTextField.setFont(commonFont);
        //设置鼠标拖入
        logPathTextField.setTransferHandler(new TransferHandler() {

            public boolean importData(TransferHandler.TransferSupport support) {
                if (!canImport(support)) {
                    return false;
                }
                try {
                    Object filePathObject = support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    String filePath = filePathObject.toString();
                    if (filePath.startsWith("[")) {
                        filePath = filePath.substring(1);
                    }
                    if (filePath.endsWith("]")) {
                        filePath = filePath.substring(0, filePath.length() - 1);
                    }
                    LogUtil.i("filePath:" + filePath);
                    logPathTextField.setText("");
                    logPathTextField.setText(filePath);
                    logDecodeSavePathTextField.setText("");
                    return true;
                } catch (UnsupportedFlavorException | IOException e) {
                    LogUtil.ei("异常信息：" + ExceptionUtils.getStackTrace(e));
                }
                return false;
            }

            public boolean canImport(TransferHandler.TransferSupport support) {
                return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
            }
        });
        contentPane.add(logPathTextField, "width 230:230:");

        //选择log路径按钮
        btnSelectLogPathButton = new JButton("选择");
        btnSelectLogPathButton.setFont(commonFont);
        btnSelectLogPathButton.addActionListener(e -> {
            JFileChooser logFileChooser = new JFileChooser();
            logFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            logFileChooser.setFileSystemView(FileSystemView.getFileSystemView());
            logFileChooser.setDialogTitle("日志文件选择");
            logFileChooser.setMultiSelectionEnabled(false);
            FileNameExtensionFilter logFilter = new FileNameExtensionFilter("xlog", "xlog");
            logFileChooser.addChoosableFileFilter(logFilter);
            FileNameExtensionFilter zipLogFilter = new FileNameExtensionFilter("zip", "zip");
            logFileChooser.addChoosableFileFilter(zipLogFilter);
            int showResult = logFileChooser.showOpenDialog(DecodeFrame.this);
            if (showResult == JFileChooser.APPROVE_OPTION) {
                String logPath = logFileChooser.getSelectedFile().getAbsolutePath();
                if (logPath.endsWith("xlog") || logPath.endsWith("zip")) {
                    logPathTextField.setText(logPath);
                    logDecodeSavePathTextField.setText("");
                } else {
                    LogUtil.i("不支持解密这个文件");
                }
            }
        });
        contentPane.add(btnSelectLogPathButton, "wrap, width 100:100:100");


        //log保存路径label
        JLabel decodeLogSavePathLabelTest = new JLabel("保存路径：");
        decodeLogSavePathLabelTest.setFont(commonFont);
        contentPane.add(decodeLogSavePathLabelTest);

        //log解密后保存路径输入框
        logDecodeSavePathTextField = new JTextField();
        logDecodeSavePathTextField.setFont(commonFont);
        contentPane.add(logDecodeSavePathTextField, "width 230:230:");

        //选择log解密后保存路径按钮
        btnSelectLogDecodeSavePathButton = new JButton("选择");
        btnSelectLogDecodeSavePathButton.setFont(commonFont);
        btnSelectLogDecodeSavePathButton.addActionListener(e -> {
            JFileChooser decodeSavefileChooser = new JFileChooser();
            decodeSavefileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            decodeSavefileChooser.setFileSystemView(FileSystemView.getFileSystemView());
            decodeSavefileChooser.setDialogTitle("解密后文件保存位置选择");
            int showResult = decodeSavefileChooser.showOpenDialog(DecodeFrame.this);
            if (showResult == JFileChooser.APPROVE_OPTION) {
                String savePath = decodeSavefileChooser.getSelectedFile().getAbsolutePath();
                logDecodeSavePathTextField.setText(savePath);
            }
        });
        contentPane.add(btnSelectLogDecodeSavePathButton, "wrap, width 100:100:100");

    }

    /**
     * 增加日志密钥选择或者输入UI
     */
    private void addLogPrivateKeyView() {
        //选择密钥 选择框
        selectPrivateKeyCheckBox = new JCheckBox("选择密钥：");
        selectPrivateKeyCheckBox.setFont(commonFont);
        selectPrivateKeyCheckBox.setEnabled(true);
        selectPrivateKeyCheckBox.addItemListener(itemListener);
        contentPane.add(selectPrivateKeyCheckBox);

        //选择密钥
        selectPrivateKeyInfoComboBox = new JComboBox<>();
        selectPrivateKeyInfoComboBox.setFont(commonFont);
        selectPrivateKeyInfoComboBox.setEditable(false);
        selectPrivateKeyInfoComboBox.setEnabled(true);
        selectPrivateKeyInfoComboBox.setMaximumRowCount(5);
        selectPrivateKeyInfoComboBox.addItemListener(privateKeyItemListener);
        contentPane.add(selectPrivateKeyInfoComboBox, "width 230:230:");

        //删除密钥
        btnDeletePrivateKeyInfoButton = new JButton("删除");
        btnDeletePrivateKeyInfoButton.setFont(commonFont);
        btnDeletePrivateKeyInfoButton.addActionListener(e -> {
            if (privateKeyMode == 1) {
                deleteDecodePrivateKey(privateKeyInfo.getPrivateKeyName());
                initPrivateKey(0);
                LogUtil.ei("删除密钥成功！");
            } else {
                LogUtil.ei("请勾选选择密钥！");
            }
        });
        contentPane.add(btnDeletePrivateKeyInfoButton, "wrap, width 100:100:100");

        //选择密钥 选择框
        inputPrivateKeyCheckBox = new JCheckBox("输入密钥：");
        inputPrivateKeyCheckBox.setFont(commonFont);
        inputPrivateKeyCheckBox.setEnabled(true);
        inputPrivateKeyCheckBox.addItemListener(itemListener);
        contentPane.add(inputPrivateKeyCheckBox);

        //密钥输入框
        inputPrivateKeyTextArea = new JTextArea("请输入私钥");
        inputPrivateKeyTextArea.setFont(commonFont);
        inputPrivateKeyTextArea.setColumns(5);
        inputPrivateKeyTextArea.setLineWrap(true);
        JScrollPane inputJScrollPane = new JScrollPane(inputPrivateKeyTextArea);
        contentPane.add(inputJScrollPane, "width 230:230:, height 110");

        //保存密钥
        btnSavePrivateKeyInfoButton = new JButton("保存");
        btnSavePrivateKeyInfoButton.setFont(commonFont);
        btnSavePrivateKeyInfoButton.addActionListener(e -> {
            btnSavePrivateKeyInfoOperation();
        });
        contentPane.add(btnSavePrivateKeyInfoButton, "wrap, width 100:100:100");
    }

    /**
     * 保存或者修改密钥操作
     */
    private void btnSavePrivateKeyInfoOperation() {
        if (privateKeyMode == 1) {
            if (btnSavePrivateKeyInfoButton.getText().equals("修改")) {
                btnSavePrivateKeyInfoButton.setText("保存");
                inputPrivateKeyTextArea.setEnabled(true);
            } else {
                String inputPrivateKeyInfo = inputPrivateKeyTextArea.getText().trim();
                if (inputPrivateKeyInfo.length() > 0) {
                    addDecodePrivateKey(privateKeyInfo.getPrivateKeyName(), inputPrivateKeyInfo);
                    btnSavePrivateKeyInfoButton.setText("修改");
                    inputPrivateKeyTextArea.setEnabled(false);
                    LogUtil.ei("更新密钥成功！");
                } else {
                    LogUtil.ei("密钥不能为空！");
                }
            }
        } else if (privateKeyMode == 2){
            String inputPrivateKeyInfo = inputPrivateKeyTextArea.getText().trim();
            if (inputPrivateKeyInfo.length() > 0) {
                showSaveDialog("名称", "请输入密钥名称", inputPrivateKeyInfo);
            } else {
                LogUtil.ei("密钥不能为空！");
            }
        } else {
            LogUtil.ei("请选择密钥模式！");
        }
    }

    private final PrivateKeyInfo privateKeyInfo = new PrivateKeyInfo();

    private final ItemListener privateKeyItemListener = e -> {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            PrivateKeyInfo privateKeyInfoTemp = (PrivateKeyInfo)e.getItem();
            privateKeyInfo.setPrivateKeyName(privateKeyInfoTemp.getPrivateKeyName());
            privateKeyInfo.setPrivateKeyValue(privateKeyInfoTemp.getPrivateKeyValue());
            inputPrivateKeyTextArea.setText("");
            inputPrivateKeyTextArea.setText(privateKeyInfo.getPrivateKeyValue());
            LogUtil.i(privateKeyInfo.getAllData());
        }
    };

    /**
     * 密钥选择模式：0，未选择；1，选择密钥；2，输入密钥
     */
    private int privateKeyMode = 0;

    private final ItemListener itemListener = e -> {
        JCheckBox selectJCheckBox = (JCheckBox)(e.getSource());
        String actionCommand = selectJCheckBox.getActionCommand();
        LogUtil.i("ItemListener->getStateChange:" + e.getStateChange());
        LogUtil.i("ItemListener->getActionCommand:" + actionCommand);
        switch (actionCommand) {
            case "选择密钥：":
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    selectPrivateKeyOperation();
                } else {
                    noSelectJude();
                }
                break;
            case "输入密钥：":
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    inputPrivateKeyOperation();
                } else {
                    noSelectJude();
                }
                break;
            default:
                break;
        }
    };

    private void selectPrivateKeyOperation() {
        privateKeyMode = 1;
        btnSavePrivateKeyInfoButton.setText("修改");
        inputPrivateKeyCheckBox.setSelected(false);
        inputPrivateKeyTextArea.setText("");
        inputPrivateKeyTextArea.setText(privateKeyInfo.getPrivateKeyValue());
        inputPrivateKeyTextArea.setEnabled(false);
        selectPrivateKeyInfoComboBox.setEnabled(true);
        btnDeletePrivateKeyInfoButton.setEnabled(true);
    }

    private void inputPrivateKeyOperation() {
        privateKeyMode = 2;
        btnSavePrivateKeyInfoButton.setText("保存");
        selectPrivateKeyCheckBox.setSelected(false);
        selectPrivateKeyInfoComboBox.setEnabled(false);
        btnDeletePrivateKeyInfoButton.setEnabled(false);
        inputPrivateKeyTextArea.setEnabled(true);
    }

    private void noSelectJude() {
        if (!inputPrivateKeyCheckBox.isSelected() && !selectPrivateKeyCheckBox.isSelected()) {
            privateKeyMode = 0;
        }
    }

    public void showSaveDialog(String title, String hint, String inputPrivateKeyInfo) {
        Window window = SwingUtilities.windowForComponent( this );
        String result = (String) JOptionPane.showInputDialog(
                window,
                hint,
                title,
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                null );
        if (result != null && result.length() > 0) {
            LogUtil.ei("密钥名称:" + result);
            for (PrivateKeyInfo privateKeyInfo : privateKeyInfoData) {
                if (privateKeyInfo.getPrivateKeyName().equals(result)) {
                    LogUtil.ei("已经存在这个名称！");
                    return;
                }
            }
            int saveIndex = privateKeyInfoData.size();
            updateDecodePrivateKey(result, inputPrivateKeyInfo);
            LogUtil.ei("保存密钥成功！");
            initPrivateKey(saveIndex);
        } else {
            LogUtil.ei("保存密钥失败！");
        }
    }

    /**
     * 保存，更新密钥
     * @param name 名称
     * @param privateKey 密钥信息
     */
    private void updateDecodePrivateKey(String name, String privateKey) {
        try {
            Wini ini = getWini();
            ini.put(IniSelectionEnum.APP.getName(), name, name);
            ini.put(IniSelectionEnum.PRIVATE_KEY.getName(), name, privateKey);
            ini.store();
        } catch (IOException | URISyntaxException e) {
            LogUtil.ei("异常信息：" + ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 保存，更新密钥
     * @param name 名称
     * @param privateKey 密钥信息
     */
    private void addDecodePrivateKey(String name, String privateKey) {
        try {
            Wini ini = getWini();
            ini.put(IniSelectionEnum.APP.getName(), name, name);
            ini.put(IniSelectionEnum.PRIVATE_KEY.getName(), name, privateKey);
            ini.store();
        } catch (IOException | URISyntaxException e) {
            LogUtil.ei("异常信息：" + ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 删除密钥
     * @param name 密钥名称
     */
    private void deleteDecodePrivateKey(String name) {
        try {
            if (name == null || name.length() == 0) {
                LogUtil.ei("不能移除空值");
                return;
            }
            Wini ini = getWini();
            ini.remove(IniSelectionEnum.APP.getName(), name);
            ini.remove(IniSelectionEnum.PRIVATE_KEY.getName(), name);
            ini.store();
        } catch (IOException | URISyntaxException e) {
            LogUtil.ei("异常信息：" + ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 日志流式压缩库选择
     */
    private void decodeLogDecompressionTypeSelectView() {
        //统计提示label
        JLabel decodeHintDecompressionLabelTest = new JLabel("解压格式：");
        decodeHintDecompressionLabelTest.setFont(commonFont);
        contentPane.add(decodeHintDecompressionLabelTest);

        //选择日志流式解压方式
        selectDecompressionComboBox = new JComboBox<>();
        selectDecompressionComboBox.setFont(commonFont);
        selectDecompressionComboBox.setEditable(false);
        selectDecompressionComboBox.setEnabled(true);
        selectDecompressionComboBox.setMaximumRowCount(5);
        selectDecompressionComboBox.addItem(DecompressionType.ZIP);
        selectDecompressionComboBox.addItem(DecompressionType.ZSTD);
        selectDecompressionComboBox.setSelectedIndex(0);
        selectDecompressionComboBox.addItemListener(decompressionItemListener);
        contentPane.add(selectDecompressionComboBox, "wrap, width 230:230:");
    }

    // 1:zip，2:zstd
    private DecompressionType decompressionType = DecompressionType.ZIP;

    private final ItemListener decompressionItemListener = e -> {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            decompressionType = (DecompressionType)e.getItem();
        }
    };

    /**
     * 解密统计
     */
    private void decodeLogStatisticsView() {
        //统计提示label
        JLabel decodeHintLabelTest = new JLabel("解密统计：");
        decodeHintLabelTest.setFont(commonFont);
        contentPane.add(decodeHintLabelTest);

        JPanel tollJPanel = new JPanel();
        tollJPanel.setLayout(new MigLayout("",
                "[grow,fill]" +
                        "[grow,fill]",
                "[]"));
        //解密成功统计label
        successCountLabel = new JLabel("成功：0");
        successCountLabel.setFont(commonFont);
        tollJPanel.add(successCountLabel, "width 115:115:115");

        //解密失败统计label
        failedCountLabel = new JLabel("失败：0");
        failedCountLabel.setFont(commonFont);
        tollJPanel.add(failedCountLabel, "width 115:115:115");
        contentPane.add(tollJPanel, "wrap, width 230:230:");
    }

    private void decodeOperationInfoView() {
        //解密操作提示label
        JLabel decodeOperationHintLabelTest = new JLabel("操作信息：");
        decodeOperationHintLabelTest.setFont(commonFont);
        contentPane.add(decodeOperationHintLabelTest);

        //展示信息内容
        decodeInfotextPane = new JTextPane();
        decodeInfotextPane.setFont(commonFont);
        decodeInfotextPane.setEditable(true);
        //右键菜单
        rightMenu = new JPopupMenu();  //弹出式菜单
        copyItem = new JMenuItem("复制");
        clearItem = new JMenuItem("清空");
        rightMenu.add(copyItem);
        rightMenu.add(clearItem);
        rightMenu.setPreferredSize(new Dimension(95, rightMenu.getPreferredSize().height));
        //设置监听
        copyItem.addActionListener(rightListener);
        clearItem.addActionListener(rightListener);
        //滚动面板
        JScrollPane scrollPane = new JScrollPane(decodeInfotextPane);
        contentPane.add(scrollPane, "width 230:230:, height 150:150:, wrap");
        //为信息设置不同颜色
        hintFontAttribute = new FontAttribute(1);
        errorFontAttribute = new FontAttribute(2);
        //获取面板信息
        decodeInfoDocs = decodeInfotextPane.getDocument();
        //添加监听
        decodeInfotextPane.addMouseListener(decodeTextPaneMouseAdapter);

        //开始解密操作
        btnStartDecodeButton = new JButton("开始解密");
        btnStartDecodeButton.setFont(commonFont);
        btnStartDecodeButton.addActionListener(startDecodeListener);
        contentPane.add(btnStartDecodeButton, "cell 1 7, width 230:230:, height 40");
    }

    private int decodeSuccessCount = 0;
    private int decodeFailureCount = 0;

    private final ActionListener startDecodeListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            LogUtil.i("e.getActionCommand():" + e.getActionCommand());
            String logFilePath = logPathTextField.getText().trim();
            String saveLogPath = logDecodeSavePathTextField.getText().trim();
            String inputPrivateKeyInfo = inputPrivateKeyTextArea.getText().trim();
            if (!logFilePath.endsWith(".zip") && !logFilePath.endsWith(".xlog")) {
                LogUtil.ei("不支持此文件类型！");
                return;
            }
            File logFile = new File(logFilePath);
            if (!logFile.exists()) {
                LogUtil.ei("解密文件不存在！");
                return;
            }
            if (privateKeyMode == 0) {
                LogUtil.ei("没有勾选-选择密钥，或-输入密钥，将只解压缩！");
            }
            if (inputPrivateKeyInfo.length() == 0) {
                LogUtil.ei("解密私钥为空，将只解压缩！");
            }
            File saveLogFileTemp = new File(saveLogPath);
            if (saveLogFileTemp.exists()) {
                if (!saveLogFileTemp.isDirectory()) {
                    saveLogPath = logFile.getParentFile().getAbsolutePath() + File.separator + logFile.getName().substring(0, logFile.getName().lastIndexOf("."));
                    LogUtil.ei("选择保存的地址不是文件夹！使用默认地址。");
                }
            } else {
                if (!saveLogFileTemp.mkdirs()) {
                    saveLogPath = logFile.getParentFile().getAbsolutePath() + File.separator + logFile.getName().substring(0, logFile.getName().lastIndexOf("."));
                    LogUtil.ei("选择的保存的地址，不能创建！使用默认地址。");
                }
            }
            if (!saveLogFileTemp.getAbsolutePath().equals(saveLogPath)) {
                LogUtil.ei("保存到地址：" + saveLogPath);
            }
            File saveLogFile = new File(saveLogPath);
            if (!saveLogFile.exists()) {
                saveLogFile.mkdirs();
            }
            logDecodeSavePathTextField.setText(saveLogPath);
            updateOperationSave(logFilePath, saveLogPath, privateKeyMode, selectPrivateKeyInfoComboBox.getSelectedIndex(), inputPrivateKeyInfo);
            decodeSuccessCount = 0;
            decodeFailureCount = 0;
            decodeLogStartViewStatus();
            new UnzipTask(logFilePath, saveLogPath).execute();
        }
    };

    private void updateOperationSave(String logFilePath, String saveLogPath, int privateKeySelectMode, int selectPrivateKeySelectIndex, String inputPrivateKeyInfo) {
        try {
            Wini ini = getWini();
            ini.put(IniSelectionEnum.OPERATION_SAVE.getName(), IniOptionNameEnum.LOG_FILE_PATH.getName(), logFilePath);
            ini.put(IniSelectionEnum.OPERATION_SAVE.getName(), IniOptionNameEnum.SAVE_TO_PATH.getName(), saveLogPath);
            ini.put(IniSelectionEnum.OPERATION_SAVE.getName(), IniOptionNameEnum.PRIVATE_KEY_SELECT_MODE.getName(), privateKeySelectMode);
            ini.put(IniSelectionEnum.OPERATION_SAVE.getName(), IniOptionNameEnum.SELECT_PRIVATE_KEY_SELECT_INDEX.getName(), selectPrivateKeySelectIndex);
            ini.put(IniSelectionEnum.OPERATION_SAVE.getName(), IniOptionNameEnum.INPUT_PRIVATE_KEY_INFO.getName(), inputPrivateKeyInfo);
            ini.store();
        } catch (IOException | URISyntaxException e) {
            LogUtil.ei("异常信息：" + ExceptionUtils.getStackTrace(e));
        }
    }

    private String selectString;

    //右键菜单
    private final MouseAdapter decodeTextPaneMouseAdapter = new MouseAdapter() {

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON3) {
                if (rightMenu.isShowing()) {
                    rightMenu.setEnabled(false);
                }
                selectString = decodeInfotextPane.getSelectedText();
                if (selectString != null && selectString.length() > 0) {
                    copyItem.setEnabled(true);
                } else {
                    copyItem.setEnabled(false);
                }
                rightMenu.show(e.getComponent(), e.getX(), e.getY());
            } else {
                if (rightMenu.isShowing()) {
                    rightMenu.setEnabled(false);
                }
            }
        }
    };

    private final ActionListener rightListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            LogUtil.i("e.getActionCommand():" + e.getActionCommand());
            if (e.getActionCommand().equals("复制")) {
                Utils.setSysClipboardText(selectString);
            } else if (e.getActionCommand().equals("清空")) {
                decodeInfotextPane.setText(null);
            }
        }
    };

    private class UnzipTask extends SwingWorker<List<String>, String>{

        private final String zipFilePath;

        private final String saveFilePath;

        private final List<String> allLogFilesPath = new ArrayList<>();

        public UnzipTask(String zipFilePath, String saveFilePath) {
            this.zipFilePath = zipFilePath;
            this.saveFilePath = saveFilePath;
        }

        @Override
        protected List<String> doInBackground() {
            allLogFilesPath.clear();
            if (zipFilePath.endsWith(".zip")) {
                LogUtil.ei("开始解压文件：" + zipFilePath);
                // 防止因为解压出错，但是实际解压成功，导致不能解密。
                try {
                    ZipFile zipFile = new ZipFile(zipFilePath);
                    zipFile.extractAll(saveFilePath);
                } catch (ZipException e) {
                    LogUtil.ei("ZIP文件存在异常！");
                    LogUtil.ei("异常信息：" + ExceptionUtils.getStackTrace(e));
                }
                File saveFileDir = new File(saveFilePath);
                if (saveFileDir.isDirectory()) {
                    getAllFilePath(saveFileDir);
                    return allLogFilesPath;
                }
            } else {
                LogUtil.ei("直接增加需要解密的日志文件：" + zipFilePath);
                allLogFilesPath.add(zipFilePath);
                return allLogFilesPath;
            }
            return null;
        }



        private void getAllFilePath(File saveFileDir) {
            File[] fileArray = saveFileDir.listFiles();
            if (fileArray != null) {
                for (File file : fileArray) {
                    if (file.isDirectory()) {
                        getAllFilePath(file);
                    } else {
                        if (file.getAbsolutePath().endsWith("xlog")) {
                            LogUtil.ei("增加需要解密的日志文件：" + file.getName());
                            allLogFilesPath.add(file.getAbsolutePath());
                        }
                    }
                }
            }
        }

        @Override
        protected void done() {
            try {
                List<String> logFiles = get();
                if (logFiles != null && logFiles.size() > 0) {
                    buildDataLogFileQueue(logFiles);
                } else {
                    LogUtil.ei("没有发现需要解密的文件！");
                    decodeLogCompleteViewStatus();
                }
            } catch (InterruptedException | ExecutionException e) {
                LogUtil.ei("异常信息：" + ExceptionUtils.getStackTrace(e));
                decodeLogCompleteViewStatus();
            }
        }
    }

    private LinkedBlockingQueue<String> logFileQueue;

    private void buildDataLogFileQueue(List<String> logFiles) {
        logFileQueue  = new LinkedBlockingQueue<>(logFiles.size());
        for (String logFilePath : logFiles) {
            logFileQueue.offer(logFilePath);
        }
        pollingDecode();
    }

    private void pollingDecode() {
        if (logFileQueue != null) {
            String logFileData = logFileQueue.poll();
            if (logFileData != null) {
                startDecodeLogFile(logFileData);
            } else {
                decodeLogCompleteViewStatus();
            }
        } else {
            decodeLogCompleteViewStatus();
        }
    }

    private void startDecodeLogFile(String logFilePath) {
        String saveLogPath = logDecodeSavePathTextField.getText().trim();
        String inputPrivateKeyInfo = inputPrivateKeyTextArea.getText().trim();
        if (privateKeyMode == 0) {
            inputPrivateKeyInfo = "";
        }
        new DecodeLogTask(logFilePath, saveLogPath, inputPrivateKeyInfo).execute();
    }

    private class DecodeLogTask extends SwingWorker<String, String>{

        private final String logFilePath;

        private final String saveLogPath;

        private final String privateKeyInfo;

        public DecodeLogTask(String logFilePath, String saveLogPath, String privateKeyInfo) {
            this.logFilePath = logFilePath;
            this.saveLogPath = saveLogPath;
            this.privateKeyInfo = privateKeyInfo;
        }

        @Override
        protected String doInBackground() throws IOException {
            File logFile = new File(logFilePath);
            LogUtil.ei("开始解密：" + logFile.getAbsolutePath());
            String outFile = saveLogPath + File.separator + logFile.getName() + ".log";
            LogUtil.ei("解密后文件：" + outFile);
            XLogFileDecode.parseFile(logFilePath, outFile, privateKeyInfo);
            return logFile.getName();
        }

        @Override
        protected void done() {
            try {
                String result = get();
                if (result != null && result.length() > 0) {
                    LogUtil.ei( result + "->解密成功");
                    decodeSuccessCount++;
                }
            } catch (InterruptedException | ExecutionException e) {
                LogUtil.ei("异常信息：" + ExceptionUtils.getStackTrace(e));
                decodeFailureCount++;
            } finally {
                updateTotalView();
                pollingDecode();
            }
        }
    }

    private void updateTotalView() {
        successCountLabel.setText("成功：" + decodeSuccessCount);
        failedCountLabel.setText("失败：" + decodeFailureCount);
    }

    private void decodeLogStartViewStatus() {
        LogUtil.ei("解密开始运行");
        //日志路径输入框
        logPathTextField.setEnabled(false);
        //选择日志路径按钮
        btnSelectLogPathButton.setEnabled(false);
        //日志解密后保存路径
        logDecodeSavePathTextField.setEnabled(false);
        //选择解密后保存的路径
        btnSelectLogDecodeSavePathButton.setEnabled(false);
        //选择密钥选择框
        selectPrivateKeyCheckBox.setEnabled(false);
        //选择密钥下拉框
        selectPrivateKeyInfoComboBox.setEnabled(false);
        //删除密钥
        btnDeletePrivateKeyInfoButton.setEnabled(false);
        //输入密钥选择框
        inputPrivateKeyCheckBox.setEnabled(false);
        //密钥输入框
        inputPrivateKeyTextArea.setEnabled(false);
        //保存密钥
        btnSavePrivateKeyInfoButton.setEnabled(false);
        //开始解密按钮
        btnStartDecodeButton.setEnabled(false);
    }

    private void decodeLogCompleteViewStatus() {
        LogUtil.ei("解密完成");
        //日志路径输入框
        logPathTextField.setEnabled(true);
        //选择日志路径按钮
        btnSelectLogPathButton.setEnabled(true);
        //日志解密后保存路径
        logDecodeSavePathTextField.setEnabled(true);
        //选择解密后保存的路径
        btnSelectLogDecodeSavePathButton.setEnabled(true);
        //选择密钥选择框
        selectPrivateKeyCheckBox.setEnabled(true);
        if (privateKeyMode == 1) {
            //选择密钥下拉框
            selectPrivateKeyInfoComboBox.setEnabled(true);
            //删除密钥
            btnDeletePrivateKeyInfoButton.setEnabled(true);
            //密钥输入框
            inputPrivateKeyTextArea.setEnabled(false);
        } else {
            //密钥输入框
            inputPrivateKeyTextArea.setEnabled(true);
        }
        //输入密钥选择框
        inputPrivateKeyCheckBox.setEnabled(true);
        //保存密钥
        btnSavePrivateKeyInfoButton.setEnabled(true);
        //开始解密按钮
        btnStartDecodeButton.setEnabled(true);
    }
}
