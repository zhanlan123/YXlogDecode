package top.yinlingfeng.xlog.decode.ui;

import com.formdev.flatlaf.FlatLaf;
import javax.swing.*;

public class XLogDecodeUi {

    public static void main(String[] args) {
        SwingUtilities.invokeLater( () -> {
            // application specific UI defaults
            FlatLaf.registerCustomDefaultsSource( "top.yinlingfeng.xlog.decode.ui" );
            // create frame
            DecodeFrame frame = new DecodeFrame();
            frame.setVisible( true );
        } );
    }

}
