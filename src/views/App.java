package views;

import controllers.UserController;
import models.Paginate;
import models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

public class App extends JFrame {

    private static final int WIDTH = 300;
    private static final int HEIGHT = 300;
    private static final int PER_PAGE = 28;
    private JPanel panelBody;

    private JScrollPane scrollPane;
    private JTable table;
    private DefaultTableModel defaultTableModel;

    UserController userController = new UserController();
    private Paginate<User> paginateUsers;

    public static void main(String[] args) {
        new App();
    }

    App() {
        setSize(WIDTH * 3, HEIGHT * 2);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        init();

        setVisible(true);
    }

    private void init() {
        panelBody = new JPanel();
        panelBody.setBounds(0, 0, WIDTH*2, HEIGHT*2);
        add(panelBody);

        loadUsers();
        createTable();
        inflateTable();
    }

    private void createTable() {
        scrollPane = new JScrollPane();
        scrollPane.setBounds(0, 0, WIDTH, HEIGHT);
        scrollPane.setBackground(Color.WHITE);
        panelBody.add(scrollPane);

        scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener(){
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                JScrollBar scrollBar = (JScrollBar) e.getAdjustable();
                int extent = scrollBar.getModel().getExtent();
                int maximum = scrollBar.getModel().getMaximum();
                int limitBeforeTouch = 50;
                if(extent + e.getValue() >= maximum - limitBeforeTouch){
                    loadMore();
                }
            }
        });

        table = new JTable(){
            private static final long serialVersionUID = 1L;

            public boolean isCellEditable(int rowIndex, int vColIndex) {
                return false;
            }
        };

        table.setModel(new DefaultTableModel(
                new Object[][] { { null }, },
                new String[] { "New column" }
        ));

        scrollPane.setViewportView(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void inflateTable() {
        defaultTableModel = new DefaultTableModel();

        defaultTableModel.addColumn("id");
        defaultTableModel.addColumn("Name");
        defaultTableModel.addColumn("Phone");
        defaultTableModel.addColumn("Status");

        paginateUsers.getData().forEach(user ->
                defaultTableModel.addRow(new String[]{
                        String.valueOf(user.getId()),
                        user.getName(),
                        user.getPhone(),
                        user.getStatus()
                })
        );

        table.setModel(defaultTableModel);
    }

    private void loadUsers() {
        int initialPage = 1;
        paginateUsers = userController.getUsers(PER_PAGE, initialPage);
    }


    private void loadMore() {
        int newPage = paginateUsers.getPage() + 1;
        if (paginateUsers.getTotalPages() >= newPage) {
            Paginate<User> newUsers = userController.getUsers(paginateUsers.getPerPage(), paginateUsers.getPage() + 1);

            System.out.println("Users founded -> " + newUsers.getData().size());
            paginateUsers.getData().addAll(newUsers.getData());
            paginateUsers.setPage(newUsers.getPage());
            inflateTable();
        }
    }
}
