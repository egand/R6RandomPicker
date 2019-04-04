import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;
import java.util.prefs.Preferences;

/**
 * Created by Eric on 15/04/2017.
 */

    //TODO cambiare la GUI aggiungendo armi e icone operatori
    //TODO trovare un modo efficiente di gestire gli operatori e le relative armi
    //TODO fare un panel per gli operatori
    //TODO usare observer observable
public class R6RandomPickerFrame extends JFrame {
    private JPanel mainPanel;
    private TopPanel topPanel;
    private JButton generateButton;
    private FormattedLabel imageResult;
    private FormattedLabel opToPick;
    private JPanel resultPanel;

    /**
     * This Frame class align elements with BorderLayout.
     * Element on TOP is topPanel, that specifies what R6 RandPicker do and the operator exceptions
     * Element on CENTER is the result of random number and the operator to pick
     * Element on BOTTOM is the generate button that choose a random operator
     *
     * @param title is the title of the main window
     */
    private R6RandomPickerFrame(String title) {
        super(title);
        setSize(470, 490);
        setResizable(false);
        centreWindow(this);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(mainPanel);

        // TOP panel
        topPanel = new TopPanel();
        mainPanel.add(topPanel, BorderLayout.PAGE_START);

        // Middle Panel
        resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        imageResult = new FormattedLabel("",Component.CENTER_ALIGNMENT);
        opToPick = new FormattedLabel("",Component.CENTER_ALIGNMENT,30.0f);

        resultPanel.add(imageResult);
        resultPanel.add(opToPick);
        mainPanel.add(resultPanel, BorderLayout.CENTER);

        // Button
        generateButton = new JButton("Generate");
        generateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        generateButton.setMargin(new Insets(5, 0, 5, 0));
        generateButton.addActionListener(new ButtonListener(topPanel));
        mainPanel.add(generateButton, BorderLayout.PAGE_END);


    }

    /**
     * Centre the window on the screen
     * @param frame window to centre
     */
    public static void centreWindow(Window frame) {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);
    }

    public static void main(String[] args) {
        R6RandomPickerFrame frame = new R6RandomPickerFrame("R6 Random Picker");
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    /**
     * This Panel class set a GridLayout inside a BoxLayout,
     * where minValue and maxValue are initialized.
     */
    class TopPanel extends JPanel {
        private FormattedLabel panelDescr;
        private JTextField atkException;
        private JTextField defException;
        private JPanel gridPanel;
        private JPanel excludeOp;
        private JPanel side;
        private ButtonGroup group;
        private JRadioButton attacker;
        private JRadioButton defender;
        private JPanel config;
        private JButton saveConfig;
        private JButton resetConfig;
        private Preferences prefs;

        private TopPanel() {
            super();

            atkException = new JTextField();
            defException = new JTextField();


            prefs = Preferences.userNodeForPackage(this.getClass());

            atkException.setText(prefs.get("atkExc",""));
            defException.setText(prefs.get("defExc",""));

            // Title and descr - BoxLayout Y axis
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            add(new FormattedLabel("Generate R6 Random Operator",Component.CENTER_ALIGNMENT,20.0f));
            add(new FormattedLabel("Select if you want a random attacker or defender",Component.CENTER_ALIGNMENT));
            panelDescr = new FormattedLabel("Click on \"Generate\" to generate a random operator",Component.CENTER_ALIGNMENT);
            panelDescr.setBorder(new EmptyBorder(0,0,10,0));
            add(panelDescr);

            //Exclude operators rules
            excludeOp = new JPanel();
            excludeOp.setLayout(new BoxLayout(excludeOp,BoxLayout.Y_AXIS));
            excludeOp.add(new FormattedLabel("To exclude an operator, put his number separated by a comma (e.g. 1,3,4)",Component.CENTER_ALIGNMENT,11.0f));
            excludeOp.add(new FormattedLabel("(Recruit is 0, max 15)",Component.CENTER_ALIGNMENT,10.0f));

            // Config buttons with listeners
            saveConfig = new JButton("Save config");
            saveConfig.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    prefs.put("atkExc",getAtkException());
                    prefs.put("defExc",getDefException());
                }
            });

            resetConfig = new JButton("Reset config");
            resetConfig.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    prefs.put("atkExc","");
                    prefs.put("defExc","");
                    atkException.setText("");
                    defException.setText("");

                }
            });



            // Configuration buttons panel
            config = new JPanel();
            config.setLayout(new BoxLayout(config,BoxLayout.X_AXIS));
            config.add(saveConfig);
            config.add(Box.createRigidArea(new Dimension(10,35)));
            config.add(resetConfig);

            // Exclude operators - GridLayout
            gridPanel = new JPanel(new GridLayout(2, 2,3,3));
            gridPanel.add(new JLabel("Attackers to exclude: ",SwingConstants.RIGHT));
            gridPanel.add(atkException);
            gridPanel.add(new JLabel("Defenders to exclude: ",SwingConstants.RIGHT));
            gridPanel.add(defException);
            gridPanel.setBorder(new EmptyBorder(5, 40, 5, 70));
            gridPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            excludeOp.add(gridPanel);
            excludeOp.add(config);
            excludeOp.setBorder(BorderFactory.createTitledBorder("Exclude Operators"));
            add(excludeOp);



            // Side faction - BoxLayout X axis
            side = new JPanel();
            side.setLayout(new BoxLayout(side,BoxLayout.X_AXIS));
            group = new ButtonGroup();
            attacker = new JRadioButton("Attacker",true);
            defender = new JRadioButton("Defender");
            group.add(attacker);
            group.add(defender);
            side.add(attacker);
            side.add(defender);
            add(side);

        }

        public String getAtkException() {
            return atkException.getText();
        }
        public String getDefException() {
            return defException.getText();
        }
        public Boolean isAtkSelected() {
            return attacker.isSelected();
        }
    }

    private class FormattedLabel extends JLabel {

        private FormattedLabel(String text, float alignment) {
            super(text);
            setAlignmentX(alignment);
        }

        private FormattedLabel(String text, float alignment, float fontSize) {
            this(text,alignment);
            setFont(this.getFont().deriveFont(fontSize));
        }

    }

    private class ButtonListener implements ActionListener {
        RandomOperator op;
        TopPanel panel;
        String[] randomOp;
        BufferedImage imageOp;

        public ButtonListener(JPanel panel) {
            this.panel = (TopPanel) panel;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            op = new RandomOperator();

            try {
                if (panel.isAtkSelected()) { //attacker selected
                    randomOp = op.generateOperator('a', panel.getAtkException());

                } else { //defender selected
                    randomOp = op.generateOperator('d',panel.getDefException());
                }
                if(randomOp[0].equals("0"))
                    imageOp = ImageIO.read((URL)R6RandomPickerFrame.class.getResource("/assets/Recruit.png"));
                else {
                    imageOp = ImageIO.read((URL)R6RandomPickerFrame.class.getResource("/assets/" + randomOp[1] + ".png"));
                }
                    imageResult.setIcon(new ImageIcon(imageOp));
                    opToPick.setText(randomOp[0] + " - " + randomOp[1].toUpperCase());

            }
            catch (IOException ex) {
                imageResult.setIcon(null);
                opToPick.setText("You can't exclude all Op");
            }
            catch (Exception ex) {
                imageResult.setIcon(null);
                opToPick.setText("Error: wrong value");
            }
        }

    }
}
