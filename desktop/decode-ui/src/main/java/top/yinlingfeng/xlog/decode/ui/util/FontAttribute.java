/**   
 * @Title: FontAttribute.java 
 * @Package top.yinxueqin.adbinstall.util 
 * @Description: TODO 
 * @author yin
 * @Email 19930108yin@gmail.com   
 * @date 2018年5月10日 下午1:48:49 
 * @version V1.0   
 */
package top.yinlingfeng.xlog.decode.ui.util;

import java.awt.Color;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 * 字体的属性类
 */
public class FontAttribute{

	public final static int HINT_ATTRIBUTE = 1;
	public final static int ERROR_ATTRIBUTE = 2;
	
	private SimpleAttributeSet attrSet = null; // 属性集

	private String name = null; // 要输入的文本和字体名称

	private boolean bold = false;
	private boolean italic = false;
	private int size; // 样式和字号

	private Color foreColor = Color.BLACK, backColor = Color.WHITE; // 文字颜色和背景颜色

	public FontAttribute() {
	}
	public FontAttribute(int type) {
		switch (type) {
			case HINT_ATTRIBUTE :
				this.setForeColor(Color.BLACK);
				this.setBold(false);
				this.setItalic(false);
				this.setSize(16);
				break;
			case ERROR_ATTRIBUTE :
				this.setForeColor(Color.RED);
				this.setBold(false);
				this.setItalic(false);
				this.setSize(16);
				break;
			default :
				break;
		}
	}
	/**
	 * 返回属性集
	 * 
	 * @return
	 */
	public SimpleAttributeSet getAttrSet() {
		attrSet = new SimpleAttributeSet();
		if (name != null) {
			StyleConstants.setFontFamily(attrSet, name);
		}
		StyleConstants.setBold(attrSet, this.bold);
		StyleConstants.setItalic(attrSet, this.italic);
		StyleConstants.setFontSize(attrSet, size);
		
		if (foreColor != null)
			StyleConstants.setForeground(attrSet, foreColor);
		if (backColor != null)
			StyleConstants.setBackground(attrSet, backColor);
		return attrSet;
	}

	/**
	 * 设置属性集
	 * 
	 * @param attrSet
	 */
	public void setAttrSet(SimpleAttributeSet attrSet) {
		this.attrSet = attrSet;
	}


	public Color getForeColor() {
		return foreColor;
	}

	public void setForeColor(Color foreColor) {
		this.foreColor = foreColor;
	}

	public Color getBackColor() {
		return backColor;
	}

	public void setBackColor(Color backColor) {
		this.backColor = backColor;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * @return the bold
	 */
	public boolean isBold() {
		return bold;
	}

	/**
	 * @param bold the bold to set
	 */
	public void setBold(boolean bold) {
		this.bold = bold;
	}

	/**
	 * @return the italic
	 */
	public boolean isItalic() {
		return italic;
	}

	/**
	 * @param italic the italic to set
	 */
	public void setItalic(boolean italic) {
		this.italic = italic;
	}
	
	
}
