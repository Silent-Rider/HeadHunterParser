package com.parser;

import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class GUI {
    private static JFrame menu = new JFrame();
    private static JFrame program = new JFrame();
    private static final Logger logger = Parser.logger;
    private static JScrollPane scrollPane;

    private static final int X_INDENT = 200;
    private static final int Y_INDENT = 100;
    private static final int WIDTH = 1100;
    private static final int HEIGHT = 700;

    private static String profession;
    private static List<Vacancy> vacancies;

    static{
        initFrame(menu);
        initFrame(program);
    }

    public static void main(String[] args) {
        launch();
    }
    //Both frames common logic
    private static void initFrame(JFrame frame){
        frame.setTitle("HeadHunterParser");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setBounds(X_INDENT, Y_INDENT, WIDTH, HEIGHT);
    }
    //Main menu frame
    private static void adjustMenu(){
        Container container = menu.getContentPane();
        container.setBackground(Color.PINK);
        container.setLayout(null);

        JLabel picture = null;
        try{
            FileInputStream imageInput = new FileInputStream("label.png");
            picture = new JLabel(new ImageIcon(ImageIO.read(imageInput)));
        } catch (IOException e) {
            logger.error("Cannot find \"label.png\"");
        }
        if(picture != null) picture.setBounds(300,50, 200, 200);

        JLabel title = new JLabel("Parser");
        title.setFont(new Font("Constantia", Font.BOLD, 70));
        title.setBounds(530, 110, 300, 100);

        JLabel input = new JLabel("Введите профессию");
        input.setFont(new Font("Times New Roman", Font.ITALIC, 20));
        input.setBounds(450, 270, 200, 20);

        JTextField text = new JTextField();
        text.setBounds(300, 300, 500, 50);
        text.setFont(new Font("Constantia", Font.BOLD, 35));

        JLabel experience = new JLabel("Без опыта");
        experience.setBounds(640, 365, 150, 50);
        experience.setFont(new Font("Tahoma", Font.BOLD, 20));

        JCheckBox checkBox = new JCheckBox();
        checkBox.setSelected(true);
        checkBox.setBounds(760, 380, 20, 20);

        JButton send = new JButton("Начать поиск");
        send.setBounds(300, 360, 300, 50);
        send.setFont(new Font("Tahoma", Font.BOLD, 30));
        send.setBackground(new Color(123,244,43));
        send.addActionListener(e -> {
            Parser.changeURL(!checkBox.isSelected());
            profession = text.getText();
            if(search()) showResults();
        });

        JButton about = new JButton("О программе");
        about.setBounds(500, 600, 150, 35);
        about.setFont(new Font("Tahoma", Font.PLAIN, 17));
        about.addActionListener(e -> JOptionPane.showMessageDialog(null, "Программа была разработана Silent Rider'ом", "О программе",
                JOptionPane.INFORMATION_MESSAGE));

        fillContainer(container, picture, title, input, text, send, experience, checkBox, about);
    }
    //Result frame
    private static void adjustProgram(){
        Container container = program.getContentPane();
        container.setBackground(new Color(171, 241,207));
        container.setLayout(new BorderLayout());

        JLabel text = new JLabel(String.format("По вашему запросу найдено %d вакансий", vacancies.size()));
        text.setFont(new Font("Tahoma", Font.PLAIN, 25));
        container.add(text, BorderLayout.NORTH);

        refreshList();
        container.add(scrollPane, BorderLayout.CENTER);

        JPanel tools = new JPanel();
        tools.setBackground(new Color(255, 190, 159));
        tools.setLayout(new FlowLayout());

        JTextArea sort = new JTextArea("Отсортировать\n по доходу");
        sort.setBackground(new Color(255, 190, 159));
        sort.setFont(new Font("Tahoma", Font.BOLD, 20));

        JButton increase = new JButton("↑");
        increase.setFont(new Font("Tahoma", Font.BOLD, 30));
        increase.addActionListener(e -> {
            Collections.sort(vacancies);
            launch();
            showResults();
        });
        JButton decrease = new JButton("↓");
        decrease.setFont(new Font("Tahoma", Font.BOLD, 30));
        decrease.addActionListener(e -> {
            vacancies.sort(Collections.reverseOrder());
            launch();
            showResults();
        });

        JButton reset = new JButton("Главное меню");
        reset.setFont(new Font("Tahoma", Font.BOLD, 20));
        reset.setBackground(new Color(255,135, 77));
        reset.addActionListener(e -> launch());

        fillContainer(tools, sort, increase, decrease, reset);
        container.add(tools, BorderLayout.SOUTH);
    }
    //Simplifying filling containers
    private static void fillContainer(Container container, JComponent... components){
        for(var component: components)
            container.add(component);
    }
    //Searching vacancies
    private static boolean search(){
        try {
            vacancies = Parser.parseHeadhunter(profession);
            logger.info("Connection successfully established");
        } catch (IOException e){
            JOptionPane.showMessageDialog(null, "Произошла ошибка во время подключения к сайту",
                    "Соединение не установлено", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (IllegalArgumentException e){
            JOptionPane.showMessageDialog(null, "По указанной профессии нет вакансий",
                    "Результаты не получены", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (InterruptedException e) {
            JOptionPane.showMessageDialog(null, "Во время работы соединение было прервано",
                    "Соединение было прервано", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
    //Showing the results of searching
    private static void showResults(){
        menu.setVisible(false);
        menu.dispose();
        menu = new JFrame();
        initFrame(menu);
        adjustProgram();
        program.setVisible(true);
    }
    //Launching program
    private static void launch(){
        program.setVisible(false);
        program.dispose();
        program = new JFrame();
        initFrame(program);
        adjustMenu();
        menu.setVisible(true);
    }
    //Refreshing list of vacancies in the scroll list
    private static void refreshList(){
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.LIGHT_GRAY);
        for(int i = 0; i < vacancies.size(); i++){
            JTextArea vacancy = new JTextArea(String.format("  %d%n%s", i+1, vacancies.get(i)));
            vacancy.setFont(new Font("Tahoma", Font.BOLD, 25));
            panel.add(vacancy);
        }
        scrollPane = new JScrollPane(panel);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    }
}
