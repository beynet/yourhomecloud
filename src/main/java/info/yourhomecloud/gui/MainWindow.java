/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yourhomecloud.gui;

import info.yourhomecloud.YourHomeCloud;
import info.yourhomecloud.configuration.Configuration;
import info.yourhomecloud.configuration.HostConfigurationBean;
import info.yourhomecloud.files.FileSyncer;
import info.yourhomecloud.files.FileSyncerBuilder;
import info.yourhomecloud.hosts.TargetHost;
import info.yourhomecloud.hosts.TargetHostBuilder;
import info.yourhomecloud.network.NetworkUtils;
import info.yourhomecloud.network.broadcast.BroadcasterListener;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author beynet
 */
public class MainWindow extends javax.swing.JFrame {

    private String mainHost;
    private final CopyStatus copyStatus;

    /**
     * Creates new form MainWindow
     */
    public MainWindow() {
        initComponents();
        this.copyStatus = new CopyStatus(this, false);
        Configuration.getConfiguration().addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                configurationChanged((Configuration) o, (Configuration.Change) arg);
            }
        });
    }

    protected void configurationChanged(Configuration conf, Configuration.Change change) {
        if (Configuration.Change.MAIN_HOST.equals(change)) {
            /* Create and display the form */
            java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    ((NetworkStatus) networkStatus).updateMainHost();
                    ((NetworkStatus) networkStatus).generateText();
                }
            });
        } else if (Configuration.Change.HOSTNAME.equals(change)) {
            final JFrame current = this;
            java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    current.setTitle("yourhomecloud (" + Configuration.getConfiguration().getCurrentHostName() + ")");
                }
            });
        } else if (Configuration.Change.NETWORK_INTERFACE.equals(change)) {
            /* Create and display the form */
            java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    ((NetworkStatus) networkStatus).updateInterface();
                    ((NetworkStatus) networkStatus).generateText();
                }
            });
        } else if (Configuration.Change.OTHER_HOSTS.equals(change)) {
            /* Create and display the form */
            java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    ((NetworkStatus) networkStatus).updateOtherHosts();
                    ((NetworkStatus) networkStatus).generateText();
                }
            });
        } else if (Configuration.Change.DIRECTORIES_TO_BE_SAVED.equals(change)) {
            /* Create and display the form */
            java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    pathsToBeSaved.setModel(new DirectoriesToBeBackupedModel());
                }
            });

        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pathSelector = new javax.swing.JFileChooser();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        pathsToBeSaved = new javax.swing.JList();
        networkPanel = new javax.swing.JPanel();
        networkStatusScroll = new javax.swing.JScrollPane();
        networkStatus = new NetworkStatus();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();

        pathSelector.setFileFilter(new PathSelectorFilter());

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle(getApplicationTitle());
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Directories backuped"));

        pathsToBeSaved.setModel(new DirectoriesToBeBackupedModel());
        pathsToBeSaved.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                removeDirectoryBackupedHandler(evt);
            }
        });
        jScrollPane1.setViewportView(pathsToBeSaved);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 479, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 181, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        networkPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Network Status"));

        networkStatus.setEditable(false);
        networkStatus.setColumns(20);
        networkStatus.setRows(5);
        networkStatusScroll.setViewportView(networkStatus);

        org.jdesktop.layout.GroupLayout networkPanelLayout = new org.jdesktop.layout.GroupLayout(networkPanel);
        networkPanel.setLayout(networkPanelLayout);
        networkPanelLayout.setHorizontalGroup(
            networkPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(networkPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(networkStatusScroll, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 472, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(16, Short.MAX_VALUE))
        );
        networkPanelLayout.setVerticalGroup(
            networkPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(networkPanelLayout.createSequentialGroup()
                .add(14, 14, 14)
                .add(networkStatusScroll, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(36, Short.MAX_VALUE))
        );

        jMenu1.setText("YourHomeCloud");

        jMenuItem6.setText("change HostName");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeHostName(evt);
            }
        });
        jMenu1.add(jMenuItem6);

        jMenuItem2.setText("configure backup");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectDirectoryToBeBackuped(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem5.setText("configure network");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                configureNetwork(evt);
            }
        });
        jMenu1.add(jMenuItem5);

        jMenuItem3.setText("Scan Network");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scanNetwork(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        jMenuItem4.setText("Start Sync");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startSync(evt);
            }
        });
        jMenu1.add(jMenuItem4);

        jMenuItem7.setText("Show Copy Status");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showCopyStatus(evt);
            }
        });
        jMenu1.add(jMenuItem7);

        jMenuItem8.setText("Show known hosts");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showKnownHosts(evt);
            }
        });
        jMenu1.add(jMenuItem8);

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.META_MASK));
        jMenuItem1.setText("Quit");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quitApplication(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(29, 29, 29)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(networkPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(48, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(24, 24, 24)
                .add(networkPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 7, Short.MAX_VALUE)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void quitApplication(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quitApplication
        for (Frame frame : Frame.getFrames()) {
            if (frame.isActive()) {
                WindowEvent windowClosing = new WindowEvent(frame, WindowEvent.WINDOW_CLOSING);
                frame.dispatchEvent(windowClosing);
            }
        }
    }//GEN-LAST:event_quitApplication

    private void selectDirectoryToBeBackuped(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectDirectoryToBeBackuped
        pathSelector.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = pathSelector.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = pathSelector.getSelectedFile();
            Configuration.getConfiguration().addDirectoryToBeSaved(Paths.get(file.getPath()));
        } else {
            System.out.println("File access cancelled by user.");
        }
    }//GEN-LAST:event_selectDirectoryToBeBackuped

    private void removeDirectoryBackupedHandler(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_removeDirectoryBackupedHandler
        if (evt.getKeyCode() == KeyEvent.VK_DELETE || evt.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
            int selectedIndex = pathsToBeSaved.getSelectedIndex();
            if (selectedIndex != -1) {
                String path = (String) pathsToBeSaved.getModel().getElementAt(selectedIndex);
                Path toBeRemoved = Paths.get(path);
                Configuration.getConfiguration().removeDirectoryToBeSaved(toBeRemoved);
            }
        }
    }//GEN-LAST:event_removeDirectoryBackupedHandler

    private void scanNetwork(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scanNetwork
        try {
            final BroadcasterListener broadcasterListener = new BroadcasterListener(NetworkUtils.DEFAULT_BROADCAST_PORT);
            final Thread thread = new Thread(broadcasterListener);
            thread.start();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Unable to start broadcast listener");
        }

    }//GEN-LAST:event_scanNetwork

    private void startSync(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startSync
        HostsSelector hosts = new HostsSelector(this, true,true);
        hosts.setVisible(true);
        HostConfigurationBean host = hosts.getSelectedHost();
        if (host == null) {
            return;
        }

        // start to sync local host directories on the selected target host
        // ----------------------------------------------------------------
        final FileSyncer fs = FileSyncerBuilder.createMonodirectionalFileSyncer();
        final TargetHost targetHost;
        try {
            targetHost = TargetHostBuilder.createRMITargetHost(host.getCurrentRMIAddress(), host.getCurrentRMIPort());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "unable to obtain remote proxy error=" + ex.getMessage());
            return;
        }
        final MainWindow current = this;
        new Thread() {
            @Override
            public void run() {
                for (String dir : Configuration.getConfiguration().getDirectoriesToBeSavedSnapshot()) {
                    try {
                        fs.sync(Paths.get(dir), targetHost,copyStatus);
                    } catch (Exception ex) {
                        StringWriter sw = new StringWriter();
                        sw.append("Error during  copy \n");
                        ex.printStackTrace(new PrintWriter(sw));
                        JOptionPane.showMessageDialog(current, sw);
                    }
                }
            }
        }.start();
        copyStatus.setVisible(true);
        
    }//GEN-LAST:event_startSync

    private void configureNetwork(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_configureNetwork
        final NetworkConfiguration networkConfiguration = new NetworkConfiguration(this, true);
        networkConfiguration.setVisible(true);
        String selected = networkConfiguration.getSelectedInterface();
        if (selected != null) {
            Configuration.getConfiguration().setNetworkInterface(selected);
        }
    }//GEN-LAST:event_configureNetwork

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        YourHomeCloud.quitApplication();
    }//GEN-LAST:event_formWindowClosing

    private void changeHostName(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeHostName
        String resp = JOptionPane.showInputDialog(this, "New host name");
        if (resp != null && !"".equals(resp)) {
            Configuration.getConfiguration().setCurrentHostName(resp);
        }
    }//GEN-LAST:event_changeHostName

    private void showCopyStatus(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showCopyStatus
        copyStatus.setVisible(true);
    }//GEN-LAST:event_showCopyStatus

    private void showKnownHosts(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showKnownHosts
        HostsSelector hosts = new HostsSelector(this, true,false);
        hosts.setVisible(true);
    }//GEN-LAST:event_showKnownHosts

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainWindow().setVisible(true);
            }
        });
    }

    private String getApplicationTitle() {
        String currentHostName = Configuration.getConfiguration().getCurrentHostName();
        if (currentHostName == null || "".equals(currentHostName)) {
            return YOURHOMECLOUD;
        } else {
            return YOURHOMECLOUD + " (" + currentHostName + ")";
        }
    }
    private final static String YOURHOMECLOUD = "YourHomeCloud";
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel networkPanel;
    private javax.swing.JTextArea networkStatus;
    private javax.swing.JScrollPane networkStatusScroll;
    private javax.swing.JFileChooser pathSelector;
    private javax.swing.JList pathsToBeSaved;
    // End of variables declaration//GEN-END:variables
}
