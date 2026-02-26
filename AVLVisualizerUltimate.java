import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

class AVLNode {
    String key;
    float cgpa;
    int height;
    AVLNode left, right;

    AVLNode(String key, float cgpa) {
        this.key = key;
        this.cgpa = cgpa;
        height = 1;
    }
}

class AVLTree {
    AVLNode root;
    AVLNode highlighted = null;

    int height(AVLNode n) {
        return n == null ? 0 : n.height;
    }

    int getBalance(AVLNode n) {
        return n == null ? 0 : height(n.left) - height(n.right);
    }

    AVLNode rightRotate(AVLNode y) {
        AVLNode x = y.left;
        AVLNode T2 = x.right;

        x.right = y;
        y.left = T2;

        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;

        return x;
    }

    AVLNode leftRotate(AVLNode x) {
        AVLNode y = x.right;
        AVLNode T2 = y.left;

        y.left = x;
        x.right = T2;

        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;

        return y;
    }

    AVLNode insert(AVLNode node, String key, float cgpa) {
        if (node == null) return new AVLNode(key, cgpa);

        if (key.compareTo(node.key) < 0)
            node.left = insert(node.left, key, cgpa);
        else if (key.compareTo(node.key) > 0)
            node.right = insert(node.right, key, cgpa);
        else
            return node;

        node.height = 1 + Math.max(height(node.left), height(node.right));
        int balance = getBalance(node);

        if (balance > 1 && key.compareTo(node.left.key) < 0)
            return rightRotate(node);

        if (balance < -1 && key.compareTo(node.right.key) > 0)
            return leftRotate(node);

        if (balance > 1 && key.compareTo(node.left.key) > 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        if (balance < -1 && key.compareTo(node.right.key) < 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    AVLNode minValueNode(AVLNode node) {
        AVLNode current = node;
        while (current.left != null)
            current = current.left;
        return current;
    }

    AVLNode delete(AVLNode root, String key) {
        if (root == null) return root;

        if (key.compareTo(root.key) < 0)
            root.left = delete(root.left, key);
        else if (key.compareTo(root.key) > 0)
            root.right = delete(root.right, key);
        else {
            if ((root.left == null) || (root.right == null)) {
                AVLNode temp = (root.left != null) ? root.left : root.right;
                if (temp == null) {
                    root = null;
                } else {
                    root = temp;
                }
            } else {
                AVLNode temp = minValueNode(root.right);
                root.key = temp.key;
                root.cgpa = temp.cgpa;
                root.right = delete(root.right, temp.key);
            }
        }

        if (root == null) return root;

        root.height = 1 + Math.max(height(root.left), height(root.right));
        int balance = getBalance(root);

        if (balance > 1 && getBalance(root.left) >= 0)
            return rightRotate(root);

        if (balance > 1 && getBalance(root.left) < 0) {
            root.left = leftRotate(root.left);
            return rightRotate(root);
        }

        if (balance < -1 && getBalance(root.right) <= 0)
            return leftRotate(root);

        if (balance < -1 && getBalance(root.right) > 0) {
            root.right = rightRotate(root.right);
            return leftRotate(root);
        }

        return root;
    }

    AVLNode search(AVLNode node, String key) {
        if (node == null || node.key.equals(key))
            return node;

        if (key.compareTo(node.key) < 0)
            return search(node.left, key);
        return search(node.right, key);
    }

    void inorder(AVLNode node, ArrayList<String> list) {
        if (node != null) {
            inorder(node.left, list);
            list.add(node.key);
            inorder(node.right, list);
        }
    }
}

class TreePanel extends JPanel {
    AVLTree tree;

    TreePanel(AVLTree tree) {
        this.tree = tree;
        setBackground(new Color(25, 25, 25));
    }

    void draw(Graphics2D g, AVLNode node, int x, int y, int gap) {
        if (node == null) return;

        if (node == tree.highlighted)
            g.setColor(Color.RED);
        else
            g.setColor(new Color(0, 200, 255));

        g.fillOval(x - 25, y - 25, 50, 50);

        g.setColor(Color.WHITE);
        g.drawString(node.key, x - 15, y);
        g.drawString("BF:" + tree.getBalance(node), x - 20, y + 15);

        if (node.left != null) {
            g.drawLine(x, y, x - gap, y + 70);
            draw(g, node.left, x - gap, y + 80, gap / 2);
        }

        if (node.right != null) {
            g.drawLine(x, y, x + gap, y + 70);
            draw(g, node.right, x + gap, y + 80, gap / 2);
        }
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw((Graphics2D) g, tree.root, getWidth() / 2, 60, getWidth() / 4);
    }
}

public class AVLVisualizerUltimate {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AVLTree tree = new AVLTree();

            JFrame frame = new JFrame("AVL Tree Visualizer ULTIMATE");
            frame.setSize(1100, 750);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            TreePanel panel = new TreePanel(tree);

            JTextField keyField = new JTextField(10);
            JTextField cgpaField = new JTextField(5);

            JButton insertBtn = new JButton("Insert");
            JButton deleteBtn = new JButton("Delete");
            JButton searchBtn = new JButton("Search");
            JButton clearBtn = new JButton("Clear");
            JButton inorderBtn = new JButton("Inorder");

            JLabel status = new JLabel("Ready");
            status.setForeground(Color.WHITE);

            JPanel top = new JPanel();
            top.setBackground(new Color(15, 15, 15));

            JLabel keyLabel = new JLabel("Name:");
            keyLabel.setForeground(Color.WHITE);
            JLabel cgLabel = new JLabel("CGPA:");
            cgLabel.setForeground(Color.WHITE);

            top.add(keyLabel);
            top.add(keyField);
            top.add(cgLabel);
            top.add(cgpaField);
            top.add(insertBtn);
            top.add(deleteBtn);
            top.add(searchBtn);
            top.add(inorderBtn);
            top.add(clearBtn);
            top.add(status);

            insertBtn.addActionListener(e -> {
                try {
                    tree.root = tree.insert(tree.root, keyField.getText(),
                            Float.parseFloat(cgpaField.getText()));
                    tree.highlighted = null;
                    status.setText("Inserted: " + keyField.getText());
                    panel.repaint();
                } catch (Exception ex) {}
            });

            deleteBtn.addActionListener(e -> {
                tree.root = tree.delete(tree.root, keyField.getText());
                tree.highlighted = null;
                status.setText("Deleted: " + keyField.getText());
                panel.repaint();
            });

            searchBtn.addActionListener(e -> {
                tree.highlighted = tree.search(tree.root, keyField.getText());
                status.setText(tree.highlighted != null ?
                        "Found: " + keyField.getText() :
                        "Not Found");
                panel.repaint();
            });

            inorderBtn.addActionListener(e -> {
                ArrayList<String> list = new ArrayList<>();
                tree.inorder(tree.root, list);
                status.setText("Inorder: " + list.toString());
            });

            clearBtn.addActionListener(e -> {
                tree.root = null;
                tree.highlighted = null;
                status.setText("Tree Cleared");
                panel.repaint();
            });

            frame.add(top, BorderLayout.NORTH);
            frame.add(panel, BorderLayout.CENTER);
            frame.setVisible(true);
        });
    }
}