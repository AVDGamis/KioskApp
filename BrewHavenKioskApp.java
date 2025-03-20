import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.net.URL;
import java.io.File;
import javax.swing.plaf.basic.BasicScrollBarUI;

/**
 * BrewHavenKioskApp - A professional coffee shop kiosk application
 * 
 * Features:
 * - Modern UI with smooth animations and transitions
 * - Product catalog with categories and detailed product information
 * - Shopping cart with quantity adjustment
 * - Loyalty program integration
 * - Order customization
 * - Checkout and payment processing
 * 
 * @author Brew Haven Development Team
 * @version 2.1
 */
public class BrewHavenKioskApp {
    // Main components
    private JFrame mainFrame;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JPanel glassPane;
    
    // Colors based on a professional color palette
    private final Color BH_RED = new Color(180, 30, 30);         // Primary brand color
    private final Color BH_DARK_RED = new Color(140, 20, 20);    // Darker accent
    private final Color BH_WHITE = new Color(255, 255, 255);     // Pure white
    private final Color BH_LIGHT_GRAY = new Color(248, 248, 248); // Light gray for backgrounds
    private final Color BH_MEDIUM_GRAY = new Color(230, 230, 230); // Medium gray for borders
    private final Color BH_DARK_GRAY = new Color(80, 80, 80);    // Dark gray for text
    private final Color BH_ACCENT = new Color(255, 200, 0);      // Gold accent color
    private final Color BH_HOVER = new Color(245, 245, 245);     // Hover state color
    private final Color BH_GREEN = new Color(40, 160, 40);       // Green for positive actions
    private final Color BH_LIGHT_GREEN = new Color(240, 255, 240); // Light green for positive feedback
    private final Color BH_LIGHT_RED = new Color(255, 240, 240); // Light red for negative feedback
    
    // Additional formal colors
    private final Color BH_BACKGROUND_GRADIENT_START = new Color(245, 245, 250); // Light formal gray-blue
    private final Color BH_BACKGROUND_GRADIENT_END = new Color(235, 235, 245);   // Slightly darker formal gray-blue
    private final Color BH_GOLD = new Color(218, 165, 32);       // Gold accent for formal look
    
    // Fonts
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 28);
    private final Font SUBTITLE_FONT = new Font("Segoe UI", Font.BOLD, 22);
    private final Font HEADING_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    
    // Cart and order tracking
    private List<CartItem> cartItems = new ArrayList<>();
    private double cartTotal = 0.0;
    private JPanel cartItemsPanel;
    private JLabel cartTotalLabel;
    private JButton cartButton;
    
    // Menu categories and items
    private Map<String, List<MenuItem>> menuCategories = new HashMap<>();
    
    // Loyalty points
    private int loyaltyPoints = 150;
    private boolean useLoyaltyDiscount = false;
    
    // Order type tracking
    private String orderType = "Dine In"; // Default order type
    
    // Animation components
    private List<FloatingItem> floatingItems = new ArrayList<>();
    private Timer animationTimer;
    
    // Folder for images
    private final String IMAGE_FOLDER = "images/";
    
    /**
     * Constructor - Initializes the kiosk application
     */
    public BrewHavenKioskApp() {
        // Set system look and feel for better integration
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Initialize menu data
        initializeMenuData();
        
        // Initialize the main frame
        mainFrame = new JFrame("Bispos Bon Appétit");
        mainFrame.setSize(1024, 768);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setIconImage(createAppIcon());
        
        // Set up glass pane for floating animations
        glassPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Paint all floating items
                synchronized(floatingItems) {
                    for (FloatingItem item : floatingItems) {
                        item.paint(g2d);
                    }
                }
            }
        };
        glassPane.setOpaque(false);
        mainFrame.setGlassPane(glassPane);
        glassPane.setVisible(true);
        
        // Start animation timer
        animationTimer = new Timer();
        animationTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateAnimations();
            }
        }, 0, 16); // ~60fps
        
        // Create card layout for multiple screens
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Create and add screens
        mainPanel.add(createWelcomeScreen(), "welcome");
        mainPanel.add(createMenuScreen(), "menu");
        mainPanel.add(createCustomizeScreen(), "customize");
        mainPanel.add(createCartScreen(), "cart");
        mainPanel.add(createCheckoutScreen(), "checkout");
        mainPanel.add(createOrderConfirmationScreen(), "confirmation");
        mainPanel.add(createLoyaltyScreen(), "loyalty");
        mainPanel.add(createOrderTypeScreen(), "orderType");
        
        // Add main panel to frame
        mainPanel.add(createAboutScreen(), "about");
        mainFrame.add(mainPanel);
        
        // Set full screen mode
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // Show the welcome screen first
        cardLayout.show(mainPanel, "welcome");
    }
    
    /**
     * Creates an application icon
     */
    private Image createAppIcon() {
        BufferedImage icon = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = icon.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw a coffee cup icon
        g.setColor(BH_RED);
        g.fillRoundRect(15, 10, 34, 40, 6, 6);
        
        // Cup handle
        g.fillRoundRect(49, 20, 10, 20, 5, 5);
        
        // Steam
        g.setColor(BH_WHITE);
        g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawArc(22, 5, 8, 8, 0, 180);
        g.drawArc(32, 3, 8, 8, 0, 180);
        g.drawArc(42, 5, 8, 8, 0, 180);
        
        // Cup highlight
        g.setColor(new Color(255, 255, 255, 60));
        g.fillRoundRect(20, 15, 10, 30, 5, 5);
        
        g.dispose();
        return icon;
    }
    
    /**
     * Updates all animations
     */
    private void updateAnimations() {
        boolean needsRepaint = false;
        
        synchronized(floatingItems) {
            Iterator<FloatingItem> iterator = floatingItems.iterator();
            while (iterator.hasNext()) {
                FloatingItem item = iterator.next();
                item.update();
                
                if (item.isDone()) {
                    iterator.remove();
                }
                
                needsRepaint = true;
            }
        }
        
        if (needsRepaint) {
            glassPane.repaint();
        }
    }
    
    /**
     * Adds a floating animation to the glass pane
     * @param isAddition true for addition animation, false for removal
     */
    private void addFloatingAnimation(boolean isAddition) {
        synchronized(floatingItems) {
            // Create a random position for the floating item
            int startX = (int)(Math.random() * (mainFrame.getWidth() - 100)) + 50;
            int startY = mainFrame.getHeight() - 50;
            
            // Create a new floating item with visual indicator of addition/removal
            FloatingItem item = new FloatingItem(startX, startY, isAddition);
            floatingItems.add(item);
        }
    }
    
    /**
     * Creates the order type selection screen
     * @return JPanel containing the order type screen
     */
    private JPanel createOrderTypeScreen() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                
                // Create a gradient background for formal appearance
                GradientPaint gp = new GradientPaint(
                    0, 0, BH_BACKGROUND_GRADIENT_START,
                    0, getHeight(), BH_BACKGROUND_GRADIENT_END);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Add subtle pattern overlay
                g2d.setColor(new Color(255, 255, 255, 30));
                int patternSize = 20;
                for (int x = 0; x < getWidth(); x += patternSize) {
                    for (int y = 0; y < getHeight(); y += patternSize) {
                        g2d.drawLine(x, y, x + patternSize/2, y);
                    }
                }
            }
        };
        
        panel.setLayout(new BorderLayout());
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setPreferredSize(new Dimension(1024, 100));
        
        JLabel titleLabel = new JLabel("Select Order Type", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(BH_RED);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Center content with main options
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        
        JPanel optionsPanel = new JPanel(new GridLayout(1, 2, 60, 0));
        optionsPanel.setOpaque(false);
        
        // Dine In option
        JPanel dineInPanel = createOrderTypeOptionPanel("Dine In", true);
        
        // Take Out option
        JPanel takeOutPanel = createOrderTypeOptionPanel("Take Out", false);
        
        optionsPanel.add(dineInPanel);
        optionsPanel.add(takeOutPanel);
        
        centerPanel.add(optionsPanel);
        panel.add(centerPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Creates a panel for a single order type option
     * @param type The order type (e.g., "Dine In" or "Take Out")
     * @param isDineIn True if this is the Dine In option
     * @return JPanel containing the order type option
     */
    private JPanel createOrderTypeOptionPanel(String type, boolean isDineIn) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BH_WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BH_GOLD, 2),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));
        
        // Create icon
        BufferedImage iconImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = iconImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g.setColor(BH_RED);
        if (isDineIn) {
            // Table icon for dine in
            g.fillRoundRect(25, 60, 50, 10, 5, 5);  // Table surface
            g.fillRect(45, 70, 10, 25);            // Table leg
            g.fillOval(35, 30, 15, 15);           // Plate
            g.fillOval(55, 30, 15, 15);           // Cup
        } else {
            // Bag icon for take out
            g.fillRoundRect(30, 30, 40, 45, 10, 10);  // Bag
            g.fillRect(20, 30, 60, 10);              // Bag top
            g.drawArc(35, 20, 30, 20, 0, 180);      // Handle
        }
        g.dispose();
        
        JLabel iconLabel = new JLabel(new ImageIcon(iconImage));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel typeLabel = new JLabel(type);
        typeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        typeLabel.setForeground(BH_DARK_RED);
        typeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JButton selectButton = createStyledButton("Select", 200, 50);
        selectButton.setFont(HEADING_FONT);
        selectButton.setBackground(BH_RED);
        selectButton.setForeground(BH_WHITE);
        selectButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        selectButton.addActionListener(e -> {
            orderType = type;
            animateTransition("menu");
        });
        
        panel.add(iconLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(typeLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        panel.add(selectButton);
        
        // Add hover effect
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BH_RED, 2),
                    BorderFactory.createEmptyBorder(30, 30, 30, 30)
                ));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BH_GOLD, 2),
                    BorderFactory.createEmptyBorder(30, 30, 30, 30)
                ));
            }
        });
        
        return panel;
    }
    
    /**
     * Initializes the menu data with categories and items
     */
    private void initializeMenuData() {
        // Create directory for images if it doesn't exist
        File imageDir = new File(IMAGE_FOLDER);
        if (!imageDir.exists()) {
            imageDir.mkdirs();
        }
        
        // Create subdirectories
        new File(IMAGE_FOLDER + "coffee").mkdirs();
        new File(IMAGE_FOLDER + "tea").mkdirs();
        new File(IMAGE_FOLDER + "pastries").mkdirs();
        new File(IMAGE_FOLDER + "sandwiches").mkdirs();
        new File(IMAGE_FOLDER + "smoothies").mkdirs();
        new File(IMAGE_FOLDER + "seasonal").mkdirs();
        
        // Create menu categories with real image paths
        List<MenuItem> coffees = new ArrayList<>();
        coffees.add(new MenuItem("Signature Espresso", 3.99, "Our house blend espresso - rich, bold and smooth.", IMAGE_FOLDER + "coffee/espresso.jpg"));
        coffees.add(new MenuItem("Caramel Macchiato", 4.99, "Espresso with steamed milk, vanilla syrup and caramel drizzle.", IMAGE_FOLDER + "coffee/caramel_macchiato.jpg"));
        coffees.add(new MenuItem("Mocha Fusion", 5.49, "Espresso with chocolate, steamed milk and whipped cream.", IMAGE_FOLDER + "coffee/mocha.jpg"));
        coffees.add(new MenuItem("Cold Brew", 4.49, "Slow-steeped for 20 hours for a smooth, rich flavor.", IMAGE_FOLDER + "coffee/cold_brew.jpg"));
        coffees.add(new MenuItem("Vanilla Latte", 4.79, "Espresso with steamed milk and vanilla syrup.", IMAGE_FOLDER + "coffee/vanilla_latte.jpg"));
        coffees.add(new MenuItem("Americano", 3.49, "Espresso diluted with hot water for a rich, full-bodied flavor.", IMAGE_FOLDER + "coffee/americano.jpg"));
        
        List<MenuItem> teas = new ArrayList<>();
        teas.add(new MenuItem("Chai Tea Latte", 4.29, "Black tea infused with cinnamon, clove, and other spices with steamed milk.", IMAGE_FOLDER + "tea/chai_latte.jpg"));
        teas.add(new MenuItem("Matcha Green Tea", 4.99, "Traditional Japanese green tea powder whisked with steamed milk.", IMAGE_FOLDER + "tea/matcha.jpg"));
        teas.add(new MenuItem("Earl Grey", 3.49, "Black tea infused with bergamot essence.", IMAGE_FOLDER + "tea/earl_grey.jpg"));
        teas.add(new MenuItem("Herbal Infusion", 3.99, "Caffeine-free blend of herbs and botanicals.", IMAGE_FOLDER + "tea/herbal.jpg"));
        teas.add(new MenuItem("Jasmine Green Tea", 3.79, "Fragrant green tea with jasmine blossoms.", IMAGE_FOLDER + "tea/jasmine.jpg"));
        
        List<MenuItem> pastries = new ArrayList<>();
        pastries.add(new MenuItem("Butter Croissant", 3.29, "Flaky, buttery layers make this a perfect companion to coffee.", IMAGE_FOLDER + "pastries/croissant.jpg"));
        pastries.add(new MenuItem("Blueberry Muffin", 3.49, "Moist muffin packed with blueberries and topped with turbinado sugar.", IMAGE_FOLDER + "pastries/blueberry_muffin.jpg"));
        pastries.add(new MenuItem("Cinnamon Roll", 4.29, "Freshly baked with cream cheese frosting.", IMAGE_FOLDER + "pastries/cinnamon_roll.jpg"));
        pastries.add(new MenuItem("Chocolate Chip Cookie", 2.99, "Baked fresh daily with premium chocolate chips.", IMAGE_FOLDER + "pastries/cookie.jpg"));
        pastries.add(new MenuItem("Almond Croissant", 3.99, "Buttery croissant filled with almond cream and topped with sliced almonds.", IMAGE_FOLDER + "pastries/almond_croissant.jpg"));
        
        List<MenuItem> sandwiches = new ArrayList<>();
        sandwiches.add(new MenuItem("Avocado & Egg", 6.99, "Freshly sliced avocado, cage-free egg, and aged white cheddar on artisan bread.", IMAGE_FOLDER + "sandwiches/avocado_egg.jpg"));
        sandwiches.add(new MenuItem("Turkey & Pesto", 7.49, "Oven-roasted turkey, provolone, pesto, and sun-dried tomatoes on ciabatta.", IMAGE_FOLDER + "sandwiches/turkey_pesto.jpg"));
        sandwiches.add(new MenuItem("Caprese Panini", 6.99, "Fresh mozzarella, tomatoes, basil, and balsamic glaze on focaccia.", IMAGE_FOLDER + "sandwiches/caprese.jpg"));
        sandwiches.add(new MenuItem("Chicken Club", 7.99, "Grilled chicken, bacon, lettuce, tomato, and aioli on sourdough.", IMAGE_FOLDER + "sandwiches/chicken_club.jpg"));
        
        List<MenuItem> smoothies = new ArrayList<>();
        smoothies.add(new MenuItem("Berry Blast", 5.99, "Strawberries, blueberries, raspberries, yogurt, and honey.", IMAGE_FOLDER + "smoothies/berry.jpg"));
        smoothies.add(new MenuItem("Green Machine", 6.49, "Spinach, kale, mango, banana, and almond milk.", IMAGE_FOLDER + "smoothies/green.jpg"));
        smoothies.add(new MenuItem("Tropical Paradise", 5.99, "Pineapple, mango, banana, coconut milk, and a hint of lime.", IMAGE_FOLDER + "smoothies/tropical.jpg"));
        smoothies.add(new MenuItem("Protein Power", 6.99, "Banana, peanut butter, chocolate protein, and almond milk.", IMAGE_FOLDER + "smoothies/protein.jpg"));
        
        List<MenuItem> seasonal = new ArrayList<>();
        seasonal.add(new MenuItem("Pumpkin Spice Latte", 5.49, "Espresso with pumpkin spice syrup, steamed milk, and whipped cream.", IMAGE_FOLDER + "seasonal/pumpkin_spice.jpg"));
        seasonal.add(new MenuItem("Peppermint Mocha", 5.49, "Espresso with chocolate, peppermint syrup, and whipped cream.", IMAGE_FOLDER + "seasonal/peppermint_mocha.jpg"));
        seasonal.add(new MenuItem("Maple Pecan Scone", 3.99, "Freshly baked scone with maple glaze and pecans.", IMAGE_FOLDER + "seasonal/maple_scone.jpg"));
        seasonal.add(new MenuItem("Gingerbread Latte", 5.29, "Espresso with gingerbread syrup, steamed milk, and whipped cream.", IMAGE_FOLDER + "seasonal/gingerbread.jpg"));
        
        // Add categories to the map
        menuCategories.put("Coffee", coffees);
        menuCategories.put("Tea", teas);
        menuCategories.put("Pastries", pastries);
        menuCategories.put("Sandwiches", sandwiches);
        menuCategories.put("Smoothies", smoothies);
        menuCategories.put("Seasonal", seasonal);
        
        // Create placeholder images for all menu items if they don't exist
        createPlaceholderImagesForMenuItems();
        }
    
    
    /**
     * Creates placeholder images for menu items if they don't exist
     */
    private void createPlaceholderImagesForMenuItems() {
        for (String category : menuCategories.keySet()) {
            List<MenuItem> items = menuCategories.get(category);
            for (MenuItem item : items) {
                File imageFile = new File(item.getImagePath());
                if (!imageFile.exists()) {
                    // Create a directory for the image if it doesn't exist
                    imageFile.getParentFile().mkdirs();
                    
                    // Create and save a placeholder image
                    try {
                        BufferedImage img = createPlaceholderImageForSaving(300, 300, item.getName(), category);
                        javax.imageio.ImageIO.write(img, "jpg", imageFile);
                        System.out.println("Created placeholder image: " + imageFile.getPath());
                    } catch (Exception e) {
                        System.err.println("Error creating placeholder image for " + item.getName());
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    /**
     * Creates a placeholder image for a menu item
     * @param width Image width
     * @param height Image height
     * @param itemName Name of the item
     * @param category Category of the item
     * @return BufferedImage of the placeholder
     */
    private BufferedImage createPlaceholderImageForSaving(int width, int height, String itemName, String category) {
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bi.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Draw a gradient background
        GradientPaint gradient = new GradientPaint(
            0, 0, new Color(250, 250, 250),
            width, height, new Color(240, 240, 240)
        );
        g.setPaint(gradient);
        g.fillRect(0, 0, width, height);
        
        // Draw category-specific icon
        drawCategoryIcon(g, category, width, height);
        
        // Draw the item name
        g.setColor(BH_DARK_RED);
        g.setFont(new Font("Segoe UI", Font.BOLD, 18));
        FontMetrics metrics = g.getFontMetrics();
        int textWidth = metrics.stringWidth(itemName);
        int textX = (width - textWidth) / 2;
        int textY = height - 80;
        g.drawString(itemName, textX, textY);
        
        // Add a message about replacing the image
        String message = "Replace with your own image";
        g.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        metrics = g.getFontMetrics();
        textWidth = metrics.stringWidth(message);
        textX = (width - textWidth) / 2;
        textY = height - 50;
        g.drawString(message, textX, textY);
        
        // Add a subtle border
        g.setColor(new Color(220, 220, 220));
        g.drawRect(0, 0, width-1, height-1);
        
        g.dispose();
        return bi;
    }
    
    /**
     * Draws a category-specific icon on the placeholder image
     * @param g Graphics2D object
     * @param category Category name
     * @param width Image width
     * @param height Image height
     */
    private void drawCategoryIcon(Graphics2D g, String category, int width, int height) {
        int centerX = width / 2;
        int centerY = height / 2 - 30;
        int iconSize = width / 3;
        
        g.setColor(BH_RED);
        
        switch(category) {
            case "Coffee":
                // Draw a coffee cup
                g.fillRoundRect(centerX - iconSize/2, centerY - iconSize/2, iconSize, iconSize, 10, 10);
                g.setColor(BH_WHITE);
                g.fillOval(centerX - iconSize/4, centerY - iconSize/4, iconSize/2, iconSize/2);
                g.setColor(BH_RED);
                g.fillRoundRect(centerX + iconSize/2, centerY - iconSize/4, iconSize/6, iconSize/2, 5, 5);
                break;
                
            case "Tea":
                // Draw a tea cup
                g.fillRoundRect(centerX - iconSize/2, centerY - iconSize/3, iconSize, iconSize/2, 10, 10);
                g.fillRect(centerX - iconSize/2, centerY - iconSize/3, iconSize, iconSize/4);
                g.setColor(BH_WHITE);
                g.fillOval(centerX - iconSize/4, centerY - iconSize/6, iconSize/2, iconSize/4);
                g.setColor(BH_RED);
                g.fillRect(centerX - iconSize/2 - iconSize/10, centerY - iconSize/3 - iconSize/10, iconSize/5, iconSize/20);
                g.fillRect(centerX + iconSize/2 - iconSize/10, centerY - iconSize/3 - iconSize/10, iconSize/5, iconSize/20);
                break;
                
            case "Pastries":
                // Draw a croissant
                g.setStroke(new BasicStroke(iconSize/10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.drawArc(centerX - iconSize/2, centerY - iconSize/2, iconSize, iconSize, 0, 180);
                g.drawArc(centerX - iconSize/3, centerY - iconSize/3, iconSize*2/3, iconSize*2/3, 0, 180);
                g.drawArc(centerX - iconSize/4, centerY - iconSize/4, iconSize/2, iconSize/2, 0, 180);
                break;
                
            case "Sandwiches":
                // Draw a sandwich
                g.fillRoundRect(centerX - iconSize/2, centerY - iconSize/4, iconSize, iconSize/2, 10, 10);
                g.setColor(new Color(240, 220, 180));
                g.fillRoundRect(centerX - iconSize/2 + 5, centerY - iconSize/4 + 5, iconSize - 10, iconSize/2 - 10, 5, 5);
                g.setColor(BH_RED);
                g.fillRect(centerX - iconSize/3, centerY - 5, iconSize*2/3, 10);
                break;
                
            case "Smoothies":
                // Draw a smoothie glass
                g.fillRoundRect(centerX - iconSize/4, centerY - iconSize/2, iconSize/2, iconSize, 20, 20);
                g.setColor(BH_WHITE);
                // Remove this duplicated line that's causing the error:
                // g.fillRoundRect(centerX - iconSize/4 + 5, centerY - iconSize/2 + 5, iconSize/2 - 10, iconSize/3, 15, 15);
                g.fillRoundRect(centerX - iconSize/4 + 5, centerY - iconSize/2 + 5, iconSize/2 - 10, iconSize/3, 15, 15);
                g.setColor(BH_RED);
                g.fillRect(centerX - iconSize/3, centerY + iconSize/2 - 10, iconSize*2/3, 10);
                break;
                
            case "Seasonal":
                // Draw a snowflake
                g.setStroke(new BasicStroke(iconSize/20, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                for (int i = 0; i < 6; i++) {
                    double angle = Math.toRadians(i * 60);
                    int x1 = centerX + (int)(Math.cos(angle) * iconSize/2);
                    int y1 = centerY + (int)(Math.sin(angle) * iconSize/2);
                    g.drawLine(centerX, centerY, x1, y1);
                    
                    // Draw small lines at the end of each arm
                    double perpAngle1 = angle + Math.toRadians(60);
                    double perpAngle2 = angle - Math.toRadians(60);
                    int smallLength = iconSize/6;
                    
                    int x2 = x1 + (int)(Math.cos(perpAngle1) * smallLength);
                    int y2 = y1 + (int)(Math.sin(perpAngle1) * smallLength);
                    g.drawLine(x1, y1, x2, y2);
                    
                    int x3 = x1 + (int)(Math.cos(perpAngle2) * smallLength);
                    int y3 = y1 + (int)(Math.sin(perpAngle2) * smallLength);
                    g.drawLine(x1, y1, x3, y3);
                }
                break;
                
            default:
                // Draw a generic icon
                g.fillOval(centerX - iconSize/2, centerY - iconSize/2, iconSize, iconSize);
                g.setColor(BH_WHITE);
                g.fillOval(centerX - iconSize/4, centerY - iconSize/4, iconSize/2, iconSize/2);
        }
    }
    
    /**
     * Creates the welcome screen
     * @return JPanel containing the welcome screen
     */
    private JPanel createWelcomeScreen() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                
                // Create a gradient background for formal appearance
                GradientPaint gp = new GradientPaint(
                    0, 0, BH_BACKGROUND_GRADIENT_START,
                    0, getHeight(), BH_BACKGROUND_GRADIENT_END);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Add subtle pattern overlay
                g2d.setColor(new Color(255, 255, 255, 30));
                int patternSize = 20;
                for (int x = 0; x < getWidth(); x += patternSize) {
                    for (int y = 0; y < getHeight(); y += patternSize) {
                        g2d.drawLine(x, y, x + patternSize/2, y);
                    }
                }
            }
        };
        
        // Logo at the top with animation
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoPanel.setOpaque(false);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 20, 0));
        
        // Create a stylized logo
        JLabel logoLabel = new JLabel("Bispos Bon Appétit");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 60));
        logoLabel.setForeground(BH_RED);
         
        // Add a subtle animation to the logo
        Timer logoTimer = new Timer();
        logoTimer.scheduleAtFixedRate(new TimerTask() {
            float scale = 1.5f;
            boolean growing = false;
            
            @Override
            public void run() {
                if (growing) {
                    scale += 0.002f;
                    if (scale >= 1.05f) {
                        growing = false;
                    }
                } else {
                    scale -= 0.002f;
                    if (scale <= 0.95f) {
                        growing = true;
                    }
                }
                
                Font currentFont = logoLabel.getFont();
                float newSize = 60 * scale;
                logoLabel.setFont(currentFont.deriveFont(newSize));
            }
        }, 0, 50);
    
        // Add logo image
        String logoPath = IMAGE_FOLDER + "logo.png";
        File logoFile = new File(logoPath);
        if (!logoFile.exists()) {
            try {
                // Create a logo image if it doesn't exist
                BufferedImage logoImage = new BufferedImage(400, 200, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = logoImage.createGraphics();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw a coffee cup
                g.setColor(BH_RED);
                g.fillOval(150, 50, 100, 100);
                g.setColor(BH_WHITE);
                g.fillOval(160, 60, 80, 80);
                g.setColor(BH_RED);
                g.fillOval(170, 70, 60, 60);
                
                // Save the logo image
                logoFile.getParentFile().mkdirs();
                javax.imageio.ImageIO.write(logoImage, "png", logoFile);
                g.dispose();
            } catch (Exception e) {
                System.err.println("Error creating logo image");
                e.printStackTrace();
            }
        }
        
        // Add logo image if it exists
        if (logoFile.exists()) {
            try {
                ImageIcon logoIcon = new ImageIcon(logoFile.getPath());
                Image scaledLogo = logoIcon.getImage().getScaledInstance(100, 50, Image.SCALE_SMOOTH);
                JLabel logoImageLabel = new JLabel(new ImageIcon(scaledLogo));
                
                JPanel logoIconJPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                logoPanel.setOpaque(false);
                logoPanel.add(logoImageLabel);
                
                panel.add(logoPanel, BorderLayout.SOUTH);
            } catch (Exception e) {
                System.err.println("Error loading logo image");
                e.printStackTrace();
            }
        }
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(logoLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        logoPanel.add(textPanel);
        panel.add(logoPanel, BorderLayout.NORTH);
        
        // Center content
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 0, 20));
        buttonPanel.setOpaque(false);
        
        JButton startButton = createStyledButton("Start Order", 300, 80);
        startButton.setBackground(BH_RED);
        startButton.setForeground(BH_WHITE);
        startButton.setFont(new Font("Segoe UI", Font.BOLD, 24));
        startButton.addActionListener(e -> {
            // Redirect to order type screen instead of menu directly
            animateTransition("orderType");
        });
        
        buttonPanel.add(startButton);
        
        centerPanel.add(buttonPanel);
        panel.add(centerPanel, BorderLayout.CENTER);
        
        // Footer
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(BH_LIGHT_GRAY);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        
        JLabel footerLabel = new JLabel("Touch screen to begin your coffee journey");
        footerLabel.setFont(BODY_FONT);
        footerLabel.setForeground(BH_DARK_RED);
        footerPanel.add(footerLabel, BorderLayout.WEST);
        
       // Footer with current time
        footerPanel.setBackground(BH_LIGHT_GRAY);
        
        JLabel timeLabel = new JLabel("Time: " + LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a")));
        timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        timeLabel.setForeground(BH_RED);
        footerPanel.add(timeLabel, BorderLayout.EAST);
        
        panel.add(footerPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Creates the menu screen with centered categories
     * @return JPanel containing the menu screen
     */
    private JPanel createMenuScreen() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                
                // Create a gradient background for formal appearance
                GradientPaint gp = new GradientPaint(
                    0, 0, BH_BACKGROUND_GRADIENT_START,
                    0, getHeight(), BH_BACKGROUND_GRADIENT_END);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Add subtle pattern overlay
                g2d.setColor(new Color(255, 255, 255, 30));
                int patternSize = 20;
                for (int x = 0; x < getWidth(); x += patternSize) {
                    for (int y = 0; y < getHeight(); y += patternSize) {
                        g2d.drawLine(x, y, x + patternSize/2, y);
                    }
                }
            }
        };
        
        // Header with logo and cart button
        JPanel headerPanel = createHeader("Bispos Bon Appétit", true);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Main content with categories and menu items
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        
        // Center the categories at the top
        JPanel categoryPanel = new JPanel();
        categoryPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        categoryPanel.setBackground(new Color(255, 255, 255, 200));
        categoryPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, BH_GOLD),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Menu items below
        JPanel menuItemsPanel = new JPanel();
        menuItemsPanel.setLayout(new BoxLayout(menuItemsPanel, BoxLayout.Y_AXIS));
        menuItemsPanel.setOpaque(false);
        menuItemsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Order type indicator
        JPanel orderTypePanel = new JPanel();
        orderTypePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        orderTypePanel.setBackground(new Color(255, 255, 255, 150));
        orderTypePanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JLabel orderTypeLabel = new JLabel("Order Type: " + orderType);
        orderTypeLabel.setFont(HEADING_FONT);
        orderTypeLabel.setForeground(BH_DARK_RED);
        
        JButton changeOrderTypeButton = createStyledButton("Change", 100, 30);
        changeOrderTypeButton.setFont(SMALL_FONT);
        changeOrderTypeButton.setBackground(BH_RED);
        changeOrderTypeButton.setForeground(BH_WHITE);
        changeOrderTypeButton.addActionListener(e -> {
            animateTransition("orderType");
        });
        
        orderTypePanel.add(orderTypeLabel);
        orderTypePanel.add(Box.createRigidArea(new Dimension(10, 0)));
        orderTypePanel.add(changeOrderTypeButton);
        
        categoryPanel.add(orderTypePanel);
        categoryPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        
        // Track the currently selected category button
        final JButton[] selectedCategoryButton = new JButton[1];
        
        for (String category : menuCategories.keySet()) {
            JButton categoryButton = new JButton(category);
            categoryButton.setFont(BODY_FONT);
            categoryButton.setPreferredSize(new Dimension(120, 40));
            categoryButton.setBackground(BH_WHITE);
            categoryButton.setForeground(BH_DARK_RED);
            categoryButton.setBorderPainted(false);
            categoryButton.setFocusPainted(false);
            
            // First category is selected by default
            if (selectedCategoryButton[0] == null) {
                selectedCategoryButton[0] = categoryButton;
                categoryButton.setBackground(BH_RED);
                categoryButton.setForeground(BH_WHITE);
            }
            
            categoryButton.addActionListener(e -> {
                // Update selected button styling
                if (selectedCategoryButton[0] != null) {
                    selectedCategoryButton[0].setBackground(BH_WHITE);
                    selectedCategoryButton[0].setForeground(BH_DARK_RED);
                }
                categoryButton.setBackground(BH_RED);
                categoryButton.setForeground(BH_WHITE);
                selectedCategoryButton[0] = categoryButton;
                
                // Update menu items
                menuItemsPanel.removeAll();
                displayMenuItems(menuItemsPanel, category);
                menuItemsPanel.revalidate();
                menuItemsPanel.repaint();
            });
            
            categoryPanel.add(categoryButton);
        }
        
        // Initially display the first category
        String firstCategory = menuCategories.keySet().iterator().next();
        displayMenuItems(menuItemsPanel, firstCategory);
        
        // Create a custom scroll pane with styled scrollbars
        JScrollPane scrollPane = createStyledScrollPane(menuItemsPanel);
        
        contentPanel.add(categoryPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Creates a styled scroll pane with custom scrollbar UI
     * @param component The component to scroll
     * @return JScrollPane with custom styling
     */
    private JScrollPane createStyledScrollPane(Component component) {
        JScrollPane scrollPane = new JScrollPane(component);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        
        // Custom scrollbar UI
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = BH_MEDIUM_GRAY;
                this.trackColor = new Color(200, 200, 200, 100);
            }
            
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }
            
            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }
            
            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }
        });
        
        return scrollPane;
    }
    
    /**
     * Creates a header panel with title and optional cart button
     * @param title The title to display
     * @param showCart Whether to show the cart button
     * @return JPanel containing the header
     */
    private JPanel createHeader(String title, boolean showCart) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BH_RED);
        headerPanel.setPreferredSize(new Dimension(1024, 80));
        
        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(BH_WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        
        JButton homeButton = createStyledButton("Home", 100, 40);
        homeButton.setFont(BUTTON_FONT);
        homeButton.setBackground(BH_DARK_RED);
        homeButton.setForeground(BH_WHITE);
        homeButton.addActionListener(e -> animateTransition("welcome"));
        
        buttonPanel.add(homeButton);
        
        if (showCart) {
            // Update the cart count in real-time
            JButton cartButton = createStyledButton("Cart (" + cartItems.size() + ")", 120, 40);
            cartButton.setFont(BUTTON_FONT);
            cartButton.setBackground(BH_ACCENT);
            cartButton.setForeground(Color.BLACK);
            cartButton.addActionListener(e -> animateTransition("cart"));
            buttonPanel.add(cartButton);
        }
        
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    // Rest of the file remains the same...
    
    /**
     * Creates a styled button with consistent appearance
     * @param text Button text
     * @param width Button width
     * @param height Button height
     * @return JButton with styling applied
     */
    private JButton createStyledButton(String text, int width, int height) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(width, height));
        button.setFont(BUTTON_FONT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBackground(BH_RED);
        button.setForeground(BH_WHITE);
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(button.getBackground().darker());
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(button.getBackground().brighter());
                }
            }
        });
        
        return button;
    }
    
    /**
     * Animates a transition between screens
     * @param targetScreen The screen to transition to
     */
    private void animateTransition(String targetScreen) {
        // Simple fade transition
        Timer timer = new Timer();
        final float[] alpha = {1.0f};
        
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                alpha[0] -= 0.1f;
                
                if (alpha[0] <= 0.0f) {
                    // Switch screens when fully transparent
                    cardLayout.show(mainPanel, targetScreen);
                    
                    // Start fade-in
                    Timer fadeInTimer = new Timer();
                    final float[] fadeInAlpha = {0.0f};
                    
                    fadeInTimer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            fadeInAlpha[0] += 0.1f;
                            
                            if (fadeInAlpha[0] >= 1.0f) {
                                this.cancel();
                            }
                        }
                    }, 0, 30);
                    
                    this.cancel();
                }
            }
        }, 0, 30);
    }
    
    /**
     * Displays menu items for a specific category
     * @param container The container to add menu items to
     * @param category The category to display
     */
    private void displayMenuItems(JPanel container, String category) {
        List<MenuItem> items = menuCategories.get(category);
        
        if (items != null) {
            for (MenuItem item : items) {
                JPanel itemPanel = createMenuItemPanel(item);
                container.add(itemPanel);
                container.add(Box.createRigidArea(new Dimension(0, 15)));
            }
        }
    }
    
    /**
     * Creates a panel for a menu item
     * @param item The menu item to display
     * @return JPanel containing the menu item
     */
    private JPanel createMenuItemPanel(MenuItem item) {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setBackground(BH_WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BH_MEDIUM_GRAY, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Item image
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setPreferredSize(new Dimension(100, 100));
        imagePanel.setBackground(BH_WHITE);
        
        // Load image or create placeholder
        ImageIcon imageIcon = loadMenuItemImage(item);
        JLabel imageLabel = new JLabel(imageIcon);
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        
        // Item details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(BH_WHITE);
        
        JLabel nameLabel = new JLabel(item.getName());
        nameLabel.setFont(HEADING_FONT);
        nameLabel.setForeground(BH_DARK_RED);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel priceLabel = new JLabel("$" + new DecimalFormat("0.00").format(item.getPrice()));
        priceLabel.setFont(BODY_FONT);
        priceLabel.setForeground(BH_DARK_GRAY);
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel descLabel = new JLabel("<html><div style='width:300px'>" + item.getDescription() + "</div></html>");
        descLabel.setFont(SMALL_FONT);
        descLabel.setForeground(BH_DARK_GRAY);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        detailsPanel.add(nameLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        detailsPanel.add(priceLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        detailsPanel.add(descLabel);
        
        // Add to cart button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        
        JButton addButton = createStyledButton("Add to Cart", 120, 40);
        addButton.setBackground(BH_GREEN);
        addButton.addActionListener(e -> {
            addToCart(item);
            addFloatingAnimation(true);
        });
        
        JButton customizeButton = createStyledButton("Customize", 120, 40);
        customizeButton.setBackground(BH_DARK_RED);
        customizeButton.addActionListener(e -> {
            // Store the current item for customization
            // and navigate to customize screen
            animateTransition("customize");
        });
        
        buttonPanel.add(customizeButton);
        buttonPanel.add(addButton);
        
        panel.add(imagePanel, BorderLayout.WEST);
        panel.add(detailsPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.EAST);
        
        // Add hover effect
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBackground(BH_HOVER);
                detailsPanel.setBackground(BH_HOVER);
                imagePanel.setBackground(BH_HOVER);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBackground(BH_WHITE);
                detailsPanel.setBackground(BH_WHITE);
                imagePanel.setBackground(BH_WHITE);
            }
        });
        
        return panel;
    }
    
    /**
     * Loads an image for a menu item or creates a placeholder
     * @param item The menu item
     * @return ImageIcon for the menu item
     */
    private ImageIcon loadMenuItemImage(MenuItem item) {
        File imageFile = new File(item.getImagePath());
        
        if (imageFile.exists()) {
            try {
                ImageIcon icon = new ImageIcon(imageFile.getPath());
                Image scaledImage = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImage);
            } catch (Exception e) {
                System.err.println("Error loading image: " + imageFile.getPath());
                e.printStackTrace();
            }
        }
        
        // Create a placeholder image if the file doesn't exist
        BufferedImage placeholder = createPlaceholderImage(100, 100, item.getName());
        return new ImageIcon(placeholder);
    }
    
    /**
     * Creates a placeholder image for a menu item
     * @param width Image width
     * @param height Image height
     * @param itemName Name of the item
     * @return BufferedImage of the placeholder
     */
    private BufferedImage createPlaceholderImage(int width, int height, String itemName) {
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bi.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Fill background
        g.setColor(BH_LIGHT_GRAY);
        g.fillRect(0, 0, width, height);
        
        // Draw a coffee cup icon
        g.setColor(BH_RED);
        g.fillRoundRect(width/2 - 15, height/2 - 20, 30, 35, 5, 5);
        g.setColor(BH_WHITE);
        g.fillOval(width/2 - 10, height/2 - 15, 20, 20);
        
        // Draw item name
        g.setColor(BH_DARK_GRAY);
        g.setFont(new Font("Segoe UI", Font.BOLD, 10));
        FontMetrics fm = g.getFontMetrics();
        String shortName = itemName;
        if (fm.stringWidth(shortName) > width - 10) {
            shortName = shortName.substring(0, 10) + "...";
        }
        int textWidth = fm.stringWidth(shortName);
        g.drawString(shortName, (width - textWidth) / 2, height - 10);
        
        g.dispose();
        return bi;
    }
    
    /**
     * Adds an item to the cart
     * @param item The menu item to add
     */
    private void addToCart(MenuItem item) {
        boolean found = false;
        for (CartItem cartItem : cartItems) {
            if (cartItem.getItem().getName().equals(item.getName())) {
                cartItem.incrementQuantity();
                found = true;
                break;
            }
        }
        if (!found) {
            cartItems.add(new CartItem(item, 1));
        }
        updateCartTotal();
        updateCartButton();
        updateCartItemsPanel();
    }

    
    /**
     * Updates the cart total
     */
    private void updateCartTotal() {
        cartTotal = 0.0;
        for (CartItem item : cartItems) {
            cartTotal += item.getItem().getPrice() * item.getQuantity();
        }
        
        // Update the cart total label if it exists
        if (cartTotalLabel != null) {
            cartTotalLabel.setText("Total: $" + new DecimalFormat("0.00").format(cartTotal));
        }
    }
    
    /**
     * Updates the cart button text with current item count
     */
    private void updateCartButton() {
        cartButton.setText("Cart (" + cartItems.size() + ")");
    }
    
    /**
     * Creates the customize screen
     * @return JPanel containing the customize screen
     */
    private JPanel createCustomizeScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BH_LIGHT_GRAY);
        
        // Header
        JPanel headerPanel = createHeader("Customize Your Order", false);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Main content
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BH_WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Item details at the top
        JPanel itemDetailsPanel = new JPanel(new BorderLayout());
        itemDetailsPanel.setBackground(BH_WHITE);
        itemDetailsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BH_MEDIUM_GRAY),
            BorderFactory.createEmptyBorder(0, 0, 10, 0)
        ));
        
        JLabel customizeLabel = new JLabel("Customize Your Item");
        customizeLabel.setFont(SUBTITLE_FONT);
        customizeLabel.setForeground(BH_DARK_RED);
        itemDetailsPanel.add(customizeLabel, BorderLayout.NORTH);
        
        // Customization options in the center
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setBackground(BH_WHITE);
        
        // Size options
        JPanel sizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sizePanel.setBackground(BH_WHITE);
        
        JLabel sizeLabel = new JLabel("Size:");
        sizeLabel.setFont(BODY_FONT);
        sizePanel.add(sizeLabel);
        
        String[] sizes = {"Small", "Medium", "Large"};
        ButtonGroup sizeGroup = new ButtonGroup();
        
        for (String size : sizes) {
            JRadioButton sizeButton = new JRadioButton(size);
            sizeButton.setFont(BODY_FONT);
            sizeButton.setBackground(BH_WHITE);
            sizeGroup.add(sizeButton);
            sizePanel.add(sizeButton);
            
            // Select medium by default
            if (size.equals("Medium")) {
                sizeButton.setSelected(true);
            }
        }
        
        // Milk options
        JPanel milkPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        milkPanel.setBackground(BH_WHITE);
        
        JLabel milkLabel = new JLabel("Milk:");
        milkLabel.setFont(BODY_FONT);
        milkPanel.add(milkLabel);
        
        String[] milkTypes = {"Whole", "2%", "Skim", "Almond", "Soy", "Oat"};
        ButtonGroup milkGroup = new ButtonGroup();
        
        for (String milk : milkTypes) {
            JRadioButton milkButton = new JRadioButton(milk);
            milkButton.setFont(BODY_FONT);
            milkButton.setBackground(BH_WHITE);
            milkGroup.add(milkButton);
            milkPanel.add(milkButton);
            
            // Select 2% by default
            if (milk.equals("2%")) {
                milkButton.setSelected(true);
            }
        }
        
        // Sweetener options
        JPanel sweetenerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sweetenerPanel.setBackground(BH_WHITE);
        
        JLabel sweetenerLabel = new JLabel("Sweetener:");
        sweetenerLabel.setFont(BODY_FONT);
        sweetenerPanel.add(sweetenerLabel);
        
        String[] sweeteners = {"None", "Sugar", "Honey", "Stevia", "Sugar-Free Syrup"};
        ButtonGroup sweetenerGroup = new ButtonGroup();
        
        for (String sweetener : sweeteners) {
            JRadioButton sweetenerButton = new JRadioButton(sweetener);
            sweetenerButton.setFont(BODY_FONT);
            sweetenerButton.setBackground(BH_WHITE);
            sweetenerGroup.add(sweetenerButton);
            sweetenerPanel.add(sweetenerButton);
            
            // Select None by default
            if (sweetener.equals("None")) {
                sweetenerButton.setSelected(true);
            }
        }
        
        // Extra options
        JPanel extrasPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        extrasPanel.setBackground(BH_WHITE);
        
        JLabel extrasLabel = new JLabel("Extras:");
        extrasLabel.setFont(BODY_FONT);
        extrasPanel.add(extrasLabel);
        
        String[] extras = {"Whipped Cream", "Extra Shot", "Vanilla Syrup", "Caramel Drizzle", "Chocolate Sauce"};
        
        for (String extra : extras) {
            JCheckBox extraCheck = new JCheckBox(extra);
            extraCheck.setFont(BODY_FONT);
            extraCheck.setBackground(BH_WHITE);
            extrasPanel.add(extraCheck);
        }
        
        // Special instructions
        JPanel instructionsPanel = new JPanel(new BorderLayout());
        instructionsPanel.setBackground(BH_WHITE);
        
        JLabel instructionsLabel = new JLabel("Special Instructions:");
        instructionsLabel.setFont(BODY_FONT);
        
        JTextArea instructionsArea = new JTextArea(3, 30);
        instructionsArea.setFont(BODY_FONT);
        instructionsArea.setLineWrap(true);
        instructionsArea.setWrapStyleWord(true);
        instructionsArea.setBorder(BorderFactory.createLineBorder(BH_MEDIUM_GRAY));
        
        instructionsPanel.add(instructionsLabel, BorderLayout.NORTH);
        instructionsPanel.add(new JScrollPane(instructionsArea), BorderLayout.CENTER);
        
        // Add all option panels
        optionsPanel.add(sizePanel);
        optionsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        optionsPanel.add(milkPanel);
        optionsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        optionsPanel.add(sweetenerPanel);
        optionsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        optionsPanel.add(extrasPanel);
        optionsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        optionsPanel.add(instructionsPanel);
        
        // Buttons at the bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BH_WHITE);
        
        JButton cancelButton = createStyledButton("Cancel", 120, 40);
        cancelButton.setBackground(BH_DARK_GRAY);
        cancelButton.addActionListener(e -> animateTransition("menu"));
        
        JButton addButton = createStyledButton("Add to Cart", 150, 40);
        addButton.setBackground(BH_GREEN);
        addButton.addActionListener(e -> {
            // Add customized item to cart
            // For demo, we'll just go back to the menu
            animateTransition("menu");
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(addButton);
        
        contentPanel.add(itemDetailsPanel, BorderLayout.NORTH);
        contentPanel.add(optionsPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Creates the cart screen
     * @return JPanel containing the cart screen
     */
    private JPanel createCartScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BH_LIGHT_GRAY);
        
        // Header
        JPanel headerPanel = createHeader("Your Cart", false);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Main content
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BH_WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Cart items
        cartItemsPanel = new JPanel();
        cartItemsPanel.setLayout(new BoxLayout(cartItemsPanel, BoxLayout.Y_AXIS));
        cartItemsPanel.setBackground(BH_WHITE);
        
        // Add cart items
        updateCartItemsPanel();
        
        // Cart total and checkout button
        JPanel checkoutPanel = new JPanel(new BorderLayout());
        checkoutPanel.setBackground(BH_WHITE);
        checkoutPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BH_MEDIUM_GRAY));
        
        cartTotalLabel = new JLabel("Total: $" + new DecimalFormat("0.00").format(cartTotal));
        cartTotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        cartTotalLabel.setForeground(BH_DARK_RED);
        
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        totalPanel.setBackground(BH_WHITE);
        totalPanel.add(cartTotalLabel);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BH_WHITE);
        
        JButton continueButton = createStyledButton("Continue Shopping", 180, 40);
        continueButton.setBackground(BH_DARK_GRAY);
        continueButton.addActionListener(e -> animateTransition("menu"));
        
        JButton checkoutButton = createStyledButton("Checkout", 150, 40);
        checkoutButton.setBackground(BH_GREEN);
        checkoutButton.addActionListener(e -> {
            if (cartItems.size() > 0) {
                animateTransition("checkout");
            }
        });
        
        buttonPanel.add(continueButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(checkoutButton);
        
        checkoutPanel.add(totalPanel, BorderLayout.WEST);
        checkoutPanel.add(buttonPanel, BorderLayout.EAST);
        
        // Create a scroll pane for cart items
        JScrollPane scrollPane = createStyledScrollPane(cartItemsPanel);
        
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(checkoutPanel, BorderLayout.SOUTH);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Updates the cart items panel with current cart contents
     */
    private void updateCartItemsPanel() {
        cartItemsPanel.removeAll();
        if (cartItems.isEmpty()) {
            JPanel emptyPanel = new JPanel(new GridBagLayout());
            emptyPanel.setBackground(Color.WHITE);
            JLabel emptyLabel = new JLabel("Your cart is empty");
            emptyLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            emptyLabel.setForeground(new Color(80, 80, 80));
            emptyPanel.add(emptyLabel);
            cartItemsPanel.add(emptyPanel);
        } else {
            for (CartItem item : cartItems) {
                JPanel itemPanel = createCartItemPanel(item);
                cartItemsPanel.add(itemPanel);
                cartItemsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }
        cartItemsPanel.revalidate();
        cartItemsPanel.repaint();
    }

  
    /**
     * Creates a panel for a cart item
     * @param cartItem The cart item to display
     * @return JPanel containing the cart item
     */
    private JPanel createCartItemPanel(CartItem cartItem) {
        MenuItem item = cartItem.getItem();
        
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setBackground(BH_WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BH_MEDIUM_GRAY, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Item image
        JPanel imagePanel = new JPanel(new BorderLayout());
      // Fix for line 1610 - remove the 'a' before 'new Dimension'
imagePanel.setPreferredSize(new Dimension(80, 80));
        imagePanel.setBackground(BH_WHITE);
        
        ImageIcon imageIcon = loadMenuItemImage(item);
        JLabel imageLabel = new JLabel(imageIcon);
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        
        // Item details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(BH_WHITE);
        
        JLabel nameLabel = new JLabel(item.getName());
        nameLabel.setFont(HEADING_FONT);
        nameLabel.setForeground(BH_DARK_RED);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        double itemTotal = item.getPrice() * cartItem.getQuantity();
        JLabel priceLabel = new JLabel("$" + new DecimalFormat("0.00").format(itemTotal));
        priceLabel.setFont(BODY_FONT);
        priceLabel.setForeground(BH_DARK_GRAY);
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        detailsPanel.add(nameLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        detailsPanel.add(priceLabel);
        
        // Quantity controls
        JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        quantityPanel.setOpaque(false);
        
        JButton decreaseButton = new JButton("-");
        decreaseButton.setFont(BUTTON_FONT);
        decreaseButton.setPreferredSize(new Dimension(40, 40));
        decreaseButton.addActionListener(e -> {
            if (cartItem.getQuantity() > 1) {
                cartItem.decrementQuantity();
                updateCartTotal();
                updateCartItemsPanel();
            } else {
                cartItems.remove(cartItem);
                updateCartTotal();
                updateCartItemsPanel();
                addFloatingAnimation(false);
            }
        });
        
        JLabel quantityLabel = new JLabel(String.valueOf(cartItem.getQuantity()));
        quantityLabel.setFont(HEADING_FONT);
        quantityLabel.setHorizontalAlignment(JLabel.CENTER);
        quantityLabel.setPreferredSize(new Dimension(40, 40));
        
        JButton increaseButton = new JButton("+");
        increaseButton.setFont(BUTTON_FONT);
        increaseButton.setPreferredSize(new Dimension(40, 40));
        increaseButton.addActionListener(e -> {
            cartItem.incrementQuantity();
            updateCartTotal();
            updateCartItemsPanel();
        });
        
        JButton removeButton = new JButton("Remove");
        removeButton.setFont(SMALL_FONT);
        removeButton.setPreferredSize(new Dimension(80, 30));
        removeButton.addActionListener(e -> {
            cartItems.remove(cartItem);
            updateCartTotal();
            updateCartItemsPanel();
            addFloatingAnimation(false);
        });
        
        quantityPanel.add(decreaseButton);
        quantityPanel.add(quantityLabel);
        quantityPanel.add(increaseButton);
        quantityPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        quantityPanel.add(removeButton);
        
        panel.add(imagePanel, BorderLayout.WEST);
        panel.add(detailsPanel, BorderLayout.CENTER);
        panel.add(quantityPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Creates the checkout screen
     * @return JPanel containing the checkout screen
     */
    private JPanel createCheckoutScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BH_LIGHT_GRAY);
        
        // Header
        JPanel headerPanel = createHeader("Checkout", false);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Main content
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BH_WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Order summary
        JPanel summaryPanel = new JPanel();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
        summaryPanel.setBackground(BH_WHITE);
        summaryPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BH_MEDIUM_GRAY),
            "Order Summary",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            HEADING_FONT,
            BH_DARK_RED
        ));
        
        // Order type display
        JPanel orderTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        orderTypePanel.setBackground(BH_WHITE);
        
        JLabel orderTypeLabel = new JLabel("Order Type: " + orderType);
        orderTypeLabel.setFont(BODY_FONT);
        orderTypeLabel.setForeground(BH_DARK_RED);
        
        JButton changeOrderTypeButton = createStyledButton("Change", 80, 30);
        changeOrderTypeButton.setFont(SMALL_FONT);
        changeOrderTypeButton.addActionListener(e -> {
            animateTransition("orderType");
        });
        
        orderTypePanel.add(orderTypeLabel);
        orderTypePanel.add(changeOrderTypeButton);
        
        // Items summary
        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setBackground(BH_WHITE);
        
        for (CartItem cartItem : cartItems) {
            MenuItem item = cartItem.getItem();
            double itemTotal = item.getPrice() * cartItem.getQuantity();
            
            JPanel itemRow = new JPanel(new BorderLayout());
            itemRow.setBackground(BH_WHITE);
            
            JLabel itemLabel = new JLabel(cartItem.getQuantity() + "x " + item.getName());
            itemLabel.setFont(BODY_FONT);
            
            JLabel itemPriceLabel = new JLabel("$" + new DecimalFormat("0.00").format(itemTotal));
            itemPriceLabel.setFont(BODY_FONT);
            
            itemRow.add(itemLabel, BorderLayout.WEST);
            itemRow.add(itemPriceLabel, BorderLayout.EAST);
            
            itemsPanel.add(itemRow);
            itemsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        
        // Subtotal, tax, and total
        JPanel totalsPanel = new JPanel();
        totalsPanel.setLayout(new BoxLayout(totalsPanel, BoxLayout.Y_AXIS));
        totalsPanel.setBackground(BH_WHITE);
        totalsPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BH_MEDIUM_GRAY));
        
        double subtotal = cartTotal;
        double tax = subtotal * 0.08; // 8% tax
        total = subtotal + tax;
                
                // Apply loyalty discount if selected
        double loyaltyDiscount = 0.0;
        if (useLoyaltyDiscount && loyaltyPoints >= 100) {
            loyaltyDiscount = 5.0; //  off for 100 points
            total -= loyaltyDiscount; // Now this will work
}

        
        JPanel subtotalRow = new JPanel(new BorderLayout());
        subtotalRow.setBackground(BH_WHITE);
        JLabel subtotalLabel = new JLabel("Subtotal");
        JLabel subtotalValueLabel = new JLabel("$" + new DecimalFormat("0.00").format(subtotal));
        subtotalRow.add(subtotalLabel, BorderLayout.WEST);
        subtotalRow.add(subtotalValueLabel, BorderLayout.EAST);
        
        JPanel taxRow = new JPanel(new BorderLayout());
        taxRow.setBackground(BH_WHITE);
        JLabel taxLabel = new JLabel("Tax (8%)");
        JLabel taxValueLabel = new JLabel("$" + new DecimalFormat("0.00").format(tax));
        taxRow.add(taxLabel, BorderLayout.WEST);
        taxRow.add(taxValueLabel, BorderLayout.EAST);
        
        // Loyalty discount row (if applicable)
        JPanel discountRow = null;
        if (useLoyaltyDiscount && loyaltyDiscount > 0) {
            discountRow = new JPanel(new BorderLayout());
            discountRow.setBackground(BH_WHITE);
            JLabel discountLabel = new JLabel("Loyalty Discount");
            JLabel discountValueLabel = new JLabel("-$" + new DecimalFormat("0.00").format(loyaltyDiscount));
            discountValueLabel.setForeground(BH_GREEN);
            discountRow.add(discountLabel, BorderLayout.WEST);
            discountRow.add(discountValueLabel, BorderLayout.EAST);
        }
        
        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setBackground(BH_WHITE);
        JLabel totalLabel = new JLabel("Total");
        totalLabel.setFont(HEADING_FONT);
        JLabel totalValueLabel = new JLabel("$" + new DecimalFormat("0.00").format(total));
        totalValueLabel.setFont(HEADING_FONT);
        totalValueLabel.setForeground(BH_DARK_RED);
        totalRow.add(totalLabel, BorderLayout.WEST);
        totalRow.add(totalValueLabel, BorderLayout.EAST);
        
        totalsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        totalsPanel.add(subtotalRow);
        totalsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        totalsPanel.add(taxRow);
        
        if (discountRow != null) {
            totalsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            totalsPanel.add(discountRow);
        }
        
        totalsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        totalsPanel.add(totalRow);
        totalsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Loyalty points option
        JPanel loyaltyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        loyaltyPanel.setBackground(BH_WHITE);
        
        JCheckBox loyaltyCheckbox = new JCheckBox("Use 100 loyalty points for $5 off");
        loyaltyCheckbox.setFont(BODY_FONT);
        loyaltyCheckbox.setBackground(BH_WHITE);
        loyaltyCheckbox.setEnabled(loyaltyPoints >= 100);
        loyaltyCheckbox.setSelected(useLoyaltyDiscount);
        loyaltyCheckbox.addActionListener(e -> {
            useLoyaltyDiscount = loyaltyCheckbox.isSelected();
            // Refresh the checkout screen to update totals
            mainPanel.remove(mainPanel.getComponent(4)); // Remove current checkout screen
            mainPanel.add(createCheckoutScreen(), "checkout", 4);
            cardLayout.show(mainPanel, "checkout");
        });
        
        JLabel pointsLabel = new JLabel("(You have " + loyaltyPoints + " points)");
        pointsLabel.setFont(SMALL_FONT);
        pointsLabel.setForeground(BH_DARK_GRAY);
        
        loyaltyPanel.add(loyaltyCheckbox);
        loyaltyPanel.add(pointsLabel);
        
        // Add all panels to summary
        summaryPanel.add(orderTypePanel);
        summaryPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        summaryPanel.add(itemsPanel);
        summaryPanel.add(totalsPanel);
        summaryPanel.add(loyaltyPanel);
        
        // Payment section
        JPanel paymentPanel = new JPanel();
        paymentPanel.setLayout(new BoxLayout(paymentPanel, BoxLayout.Y_AXIS));
        paymentPanel.setBackground(BH_WHITE);
        paymentPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BH_MEDIUM_GRAY),
            "Payment Method",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            HEADING_FONT,
            BH_DARK_RED
        ));
        
        // Payment options
        JPanel paymentOptionsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        paymentOptionsPanel.setBackground(BH_WHITE);
        
        JRadioButton creditCardButton = new JRadioButton("Credit Card");
        creditCardButton.setFont(BODY_FONT);
        creditCardButton.setBackground(BH_WHITE);
        creditCardButton.setSelected(true);
        
        JRadioButton debitCardButton = new JRadioButton("Debit Card");
        debitCardButton.setFont(BODY_FONT);
        debitCardButton.setBackground(BH_WHITE);
        
        JRadioButton giftCardButton = new JRadioButton("Gift Card");
        giftCardButton.setFont(BODY_FONT);
        giftCardButton.setBackground(BH_WHITE);
        
        JRadioButton cashButton = new JRadioButton("Cash (Pay at Counter)");
        cashButton.setFont(BODY_FONT);
        cashButton.setBackground(BH_WHITE);
        
        ButtonGroup paymentGroup = new ButtonGroup();
        paymentGroup.add(creditCardButton);
        paymentGroup.add(debitCardButton);
        paymentGroup.add(giftCardButton);
        paymentGroup.add(cashButton);
        
        paymentOptionsPanel.add(creditCardButton);
        paymentOptionsPanel.add(debitCardButton);
        paymentOptionsPanel.add(giftCardButton);
        paymentOptionsPanel.add(cashButton);
        
        // Credit card details (shown by default)
        JPanel cardDetailsPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        cardDetailsPanel.setBackground(BH_WHITE);
        cardDetailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        JLabel cardNumberLabel = new JLabel("Card Number:");
        JTextField cardNumberField = new JTextField();
        
        JLabel expiryLabel = new JLabel("Expiry Date:");
        JTextField expiryField = new JTextField();
        
        JLabel cvvLabel = new JLabel("CVV:");
        JTextField cvvField = new JTextField();
        
        cardDetailsPanel.add(cardNumberLabel);
        cardDetailsPanel.add(cardNumberField);
        cardDetailsPanel.add(expiryLabel);
        cardDetailsPanel.add(expiryField);
        cardDetailsPanel.add(cvvLabel);
        cardDetailsPanel.add(cvvField);
        
        paymentPanel.add(paymentOptionsPanel);
        paymentPanel.add(cardDetailsPanel);
        
        // Show/hide card details based on selection
        ActionListener paymentOptionListener = e -> {
            cardDetailsPanel.setVisible(creditCardButton.isSelected() || debitCardButton.isSelected());
            paymentPanel.revalidate();
            paymentPanel.repaint();
        };
        
        creditCardButton.addActionListener(paymentOptionListener);
        debitCardButton.addActionListener(paymentOptionListener);
        giftCardButton.addActionListener(paymentOptionListener);
        cashButton.addActionListener(paymentOptionListener);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BH_WHITE);
        
        JButton backButton = createStyledButton("Back to Cart", 150, 40);
        backButton.setBackground(BH_DARK_GRAY);
        backButton.addActionListener(e -> animateTransition("cart"));
        
        JButton placeOrderButton = createStyledButton("Place Order", 150, 40);
        placeOrderButton.setBackground(BH_GREEN);
        placeOrderButton.addActionListener(e -> {
            // Process order and show confirmation
            if (useLoyaltyDiscount) {
                loyaltyPoints -= 100;
            }
            
            // Add points for this purchase (1 point per dollar spent)
            loyaltyPoints += (int)total;
            
            // Clear cart and show confirmation
            cartItems.clear();
            updateCartTotal();
            
            animateTransition("confirmation");
        });
        
        buttonPanel.add(backButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(placeOrderButton);
        
        // Layout the main sections
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        mainPanel.setBackground(BH_WHITE);
        mainPanel.add(summaryPanel);
        mainPanel.add(paymentPanel);
        
        contentPanel.add(mainPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Creates the order confirmation screen
     * @return JPanel containing the order confirmation screen
     */
    private JPanel createOrderConfirmationScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BH_LIGHT_GRAY);
        
        // Header
        JPanel headerPanel = createHeader("Order Confirmation", false);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Main content
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BH_WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Confirmation message
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        messagePanel.setBackground(BH_LIGHT_GREEN);
        messagePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BH_GREEN, 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Success icon
        BufferedImage checkImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = checkImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw a green circle with a white checkmark
        g.setColor(BH_GREEN);
        g.fillOval(10, 10, 80, 80);
        
        g.setColor(BH_WHITE);
        g.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(30, 50, 45, 65);
        g.drawLine(45, 65, 70, 35);
        
        g.dispose();
        
        JLabel checkLabel = new JLabel(new ImageIcon(checkImage));
        checkLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel thanksLabel = new JLabel("Thank You For Your Order!");
        thanksLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        thanksLabel.setForeground(BH_DARK_RED);
        thanksLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel orderTypeLabel = new JLabel("Order Type: " + orderType);
        orderTypeLabel.setFont(HEADING_FONT);
        orderTypeLabel.setForeground(BH_DARK_GRAY);
        orderTypeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel orderNumberLabel = new JLabel("Order #" + (int)(Math.random() * 1000 + 1000));
        orderNumberLabel.setFont(HEADING_FONT);
        orderNumberLabel.setForeground(BH_DARK_GRAY);
        orderNumberLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel instructionLabel = new JLabel("Please proceed to the counter with your receipt.");
        instructionLabel.setFont(BODY_FONT);
        instructionLabel.setForeground(BH_DARK_GRAY);
        instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel pointsEarnedLabel = new JLabel("You earned points on this purchase!");
        pointsEarnedLabel.setFont(BODY_FONT);
        pointsEarnedLabel.setForeground(BH_DARK_RED);
        pointsEarnedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel totalPointsLabel = new JLabel("Your total points: " + loyaltyPoints);
        totalPointsLabel.setFont(BODY_FONT);
        totalPointsLabel.setForeground(BH_DARK_RED);
        totalPointsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        messagePanel.add(checkLabel);
        messagePanel.add(Box.createRigidArea(new Dimension(0, 20)));
        messagePanel.add(thanksLabel);
        messagePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        messagePanel.add(orderTypeLabel);
        messagePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        messagePanel.add(orderNumberLabel);
        messagePanel.add(Box.createRigidArea(new Dimension(0, 20)));
        messagePanel.add(instructionLabel);
        messagePanel.add(Box.createRigidArea(new Dimension(0, 30)));
        messagePanel.add(pointsEarnedLabel);
        messagePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        messagePanel.add(totalPointsLabel);
        
        // Button to return to home
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        
        JButton homeButton = createStyledButton("Return to Home", 200, 50);
        homeButton.setFont(HEADING_FONT);
        homeButton.setBackground(BH_RED);
        homeButton.addActionListener(e -> animateTransition("welcome"));
        
        buttonPanel.add(homeButton);
        
        contentPanel.add(messagePanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Creates the loyalty program screen
     * @return JPanel containing the loyalty screen
     */
    private JPanel createLoyaltyScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BH_LIGHT_GRAY);
        
        // Header
        JPanel headerPanel = createHeader("Loyalty Program", false);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Main content
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BH_WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Loyalty card
        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setBackground(BH_RED);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BH_GOLD, 3),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel cardTitleLabel = new JLabel("Bispos Bon Appétit");
        cardTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        cardTitleLabel.setForeground(BH_WHITE);
        
        JLabel cardSubtitleLabel = new JLabel("LOYALTY CARD");
        cardSubtitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        cardSubtitleLabel.setForeground(BH_GOLD);
        
        JPanel cardHeaderPanel = new JPanel();
        cardHeaderPanel.setLayout(new BoxLayout(cardHeaderPanel, BoxLayout.Y_AXIS));
        cardHeaderPanel.setOpaque(false);
        cardHeaderPanel.add(cardTitleLabel);
        cardHeaderPanel.add(cardSubtitleLabel);
        
        JLabel pointsLabel = new JLabel("POINTS: " + loyaltyPoints);
        pointsLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        pointsLabel.setForeground(BH_WHITE);
        
        cardPanel.add(cardHeaderPanel, BorderLayout.NORTH);
        cardPanel.add(pointsLabel, BorderLayout.CENTER);
        
        // Rewards information
        JPanel rewardsPanel = new JPanel();
        rewardsPanel.setLayout(new BoxLayout(rewardsPanel, BoxLayout.Y_AXIS));
        rewardsPanel.setBackground(BH_WHITE);
        rewardsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BH_MEDIUM_GRAY),
            "Available Rewards",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            HEADING_FONT,
            BH_DARK_RED
        ));
        
        String[] rewards = {
            "100 points: $5 off your next purchase",
            "200 points: Free coffee of your choice",
            "300 points: Free pastry of your choice",
            "500 points: Free sandwich of your choice",
            "1000 points: Free catering box (6 coffees + 6 pastries)"
        };
        
        for (String reward : rewards) {
            JPanel rewardRow = new JPanel(new BorderLayout(10, 0));
            rewardRow.setBackground(BH_WHITE);
            
            String[] parts = reward.split(":");
            int points = Integer.parseInt(parts[0].trim().split(" ")[0]);
            
            JLabel rewardLabel = new JLabel(reward);
            rewardLabel.setFont(BODY_FONT);
            
            JButton redeemButton = createStyledButton("Redeem", 100, 30);
            redeemButton.setFont(SMALL_FONT);
            redeemButton.setEnabled(loyaltyPoints >= points);
            
            if (!redeemButton.isEnabled()) {
                redeemButton.setBackground(BH_MEDIUM_GRAY);
            }
            
            rewardRow.add(rewardLabel, BorderLayout.WEST);
            rewardRow.add(redeemButton, BorderLayout.EAST);
            
            rewardsPanel.add(rewardRow);
            rewardsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        
        // How it works section
        JPanel howItWorksPanel = new JPanel();
        howItWorksPanel.setLayout(new BoxLayout(howItWorksPanel, BoxLayout.Y_AXIS));
        howItWorksPanel.setBackground(BH_WHITE);
        howItWorksPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BH_MEDIUM_GRAY),
            "How It Works",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            HEADING_FONT,
            BH_DARK_RED
        ));
        
        String[] instructions = {
            "• Earn 1 point for every $1 spent",
            "• Redeem points for rewards at any time",
            "• Points never expire",
            "• Show your loyalty card to earn and redeem points",
            "• Sign up for our newsletter to earn bonus points"
        };
        
        for (String instruction : instructions) {
            JLabel instructionLabel = new JLabel(instruction);
            instructionLabel.setFont(BODY_FONT);
            instructionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            howItWorksPanel.add(instructionLabel);
            howItWorksPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BH_WHITE);
        
        JButton homeButton = createStyledButton("Return to Home", 150, 40);
        homeButton.setBackground(BH_RED);
        homeButton.addActionListener(e -> animateTransition("welcome"));
        
        buttonPanel.add(homeButton);
        
        // Layout the main sections
        JPanel mainPanel = new JPanel(new GridLayout(3, 1, 0, 20));
        mainPanel.setBackground(BH_WHITE);
        mainPanel.add(cardPanel);
        mainPanel.add(rewardsPanel);
        mainPanel.add(howItWorksPanel);
        
        contentPanel.add(mainPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Creates the about screen
     * @return JPanel containing the about screen
     */
    private JPanel createAboutScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BH_LIGHT_GRAY);
        
        // Header
        JPanel headerPanel = createHeader("About Bispos Bon Appétit", false);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Main content
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BH_WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // About content
        JPanel aboutPanel = new JPanel();
        aboutPanel.setLayout(new BoxLayout(aboutPanel, BoxLayout.Y_AXIS));
        aboutPanel.setBackground(BH_WHITE);
        
        JLabel titleLabel = new JLabel("Our Story");
        titleLabel.setFont(SUBTITLE_FONT);
        titleLabel.setForeground(BH_DARK_RED);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextArea storyArea = new JTextArea(
            "Bispos Bon Appétit was founded in 2010 with a simple mission: to create a haven " +
            "for coffee lovers where quality, community, and sustainability come together.\n\n" +
            
            "What started as a small corner café has grown into a beloved local chain, " +
            "but our commitment to hand-crafted beverages and personal service remains unchanged. " +
            "We source our beans directly from farmers who share our values of ethical and " +
            "sustainable production.\n\n" +
            
            "Every cup of Bispos Bon Appétit coffee represents our passion for the perfect brew " +
            "and our dedication to creating a warm, welcoming space for our community. " +
            "We're more than just a coffee shop - we're your daily retreat, your meeting spot, " +
            "and your home away from home."
        );
        storyArea.setFont(BODY_FONT);
      // Fix for line 2286 - complete the foreground setting and add the rest of the file
storyArea.setFont(BODY_FONT);
storyArea.setForeground(BH_DARK_GRAY);
storyArea.setLineWrap(true);
storyArea.setWrapStyleWord(true);
storyArea.setEditable(false);
storyArea.setBackground(BH_WHITE);
storyArea.setAlignmentX(Component.LEFT_ALIGNMENT);
storyArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

JLabel valuesLabel = new JLabel("Our Values");
valuesLabel.setFont(SUBTITLE_FONT);
valuesLabel.setForeground(BH_DARK_RED);
valuesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

String[] values = {
    "Quality: We never compromise on the quality of our ingredients or our service.",
    "Community: We strive to create spaces where people feel welcome and connected.",
    "Sustainability: We make environmentally responsible choices in everything we do.",
    "Innovation: We continuously explore new flavors and experiences for our customers.",
    "Integrity: We operate with honesty and transparency in all our relationships."
};

JPanel valuesPanel = new JPanel();
valuesPanel.setLayout(new BoxLayout(valuesPanel, BoxLayout.Y_AXIS));
valuesPanel.setBackground(BH_WHITE);
valuesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

for (String value : values) {
    JLabel valueLabel = new JLabel("• " + value);
    valueLabel.setFont(BODY_FONT);
    valueLabel.setForeground(BH_DARK_GRAY);
    valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    
    valuesPanel.add(valueLabel);
    valuesPanel.add(Box.createRigidArea(new Dimension(0, 5)));
}

JLabel contactLabel = new JLabel("Contact Us");
contactLabel.setFont(SUBTITLE_FONT);
contactLabel.setForeground(BH_DARK_RED);
contactLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

JPanel contactPanel = new JPanel(new GridLayout(4, 1, 0, 5));
contactPanel.setBackground(BH_WHITE);
contactPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

JLabel addressLabel = new JLabel("Address: 123 Coffee Lane, Brewville, BV 98765");
JLabel phoneLabel = new JLabel("Phone: (555) 123-4567");
JLabel emailLabel = new JLabel("Email: info@brewhaven.com");
JLabel websiteLabel = new JLabel("Website: www.brewhaven.com");

addressLabel.setFont(BODY_FONT);
phoneLabel.setFont(BODY_FONT);
emailLabel.setFont(BODY_FONT);
websiteLabel.setFont(BODY_FONT);

contactPanel.add(addressLabel);
contactPanel.add(phoneLabel);
contactPanel.add(emailLabel);
contactPanel.add(websiteLabel);

aboutPanel.add(titleLabel);
aboutPanel.add(Box.createRigidArea(new Dimension(0, 10)));
aboutPanel.add(storyArea);
aboutPanel.add(Box.createRigidArea(new Dimension(0, 20)));
aboutPanel.add(valuesLabel);
aboutPanel.add(Box.createRigidArea(new Dimension(0, 10)));
aboutPanel.add(valuesPanel);
aboutPanel.add(Box.createRigidArea(new Dimension(0, 20)));
aboutPanel.add(contactLabel);
aboutPanel.add(Box.createRigidArea(new Dimension(0, 10)));
aboutPanel.add(contactPanel);

// Button panel
JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
buttonPanel.setBackground(BH_WHITE);

JButton homeButton = createStyledButton("Return to Home", 150, 40);
homeButton.setBackground(BH_RED);
homeButton.addActionListener(e -> animateTransition("welcome"));

buttonPanel.add(homeButton);

// Create a scroll pane for the about content
JScrollPane scrollPane = createStyledScrollPane(aboutPanel);

contentPanel.add(scrollPane, BorderLayout.CENTER);
contentPanel.add(buttonPanel, BorderLayout.SOUTH);

panel.add(contentPanel, BorderLayout.CENTER);

return panel;
}

/**
 * Inner class for menu items
 */
private class MenuItem {
    private String name;
    private double price;
    private String description;
    private String imagePath;
    
    public MenuItem(String name, double price, String description, String imagePath) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.imagePath = imagePath;
    }
    
    public String getName() {
        return name;
    }
    
    public double getPrice() {
        return price;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getImagePath() {
        return imagePath;
    }
}

/**
 * Inner class for cart items
 */
private class CartItem {
    private MenuItem item;
    private int quantity;
    
    public CartItem(MenuItem item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }
    
    public MenuItem getItem() {
        return item;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void incrementQuantity() {
        quantity++;
    }
    
    public void decrementQuantity() {
        if (quantity > 0) {
            quantity--;
        }
    }
}

/**
 * Inner class for floating animation items
 */
private class FloatingItem {
    private int x, y;
    private int startY;
    private int alpha = 255;
    private boolean isAddition;
    
    public FloatingItem(int x, int y, boolean isAddition) {
        this.x = x;
        this.y = y;
        this.startY = y;
        this.isAddition = isAddition;
    }
    
    public void update() {
        // Move upward
        y -= 3;
        
        // Fade out
        alpha -= 5;
        
        // If completely faded out, mark as done
        if (alpha < 0) {
            alpha = 0;
        }
    }
    
    public boolean isDone() {
        return alpha <= 0 || y < 0;
    }
    
    public void paint(Graphics2D g) {
        if (alpha <= 0) return;
        
        // Set alpha for transparency
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha / 255.0f));
        
        // Draw a circle with + or - symbol
        int size = 40;
        g.setColor(isAddition ? BH_GREEN : BH_RED);
        g.fillOval(x - size/2, y - size/2, size, size);
        
        g.setColor(BH_WHITE);
        g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        if (isAddition) {
            // Draw + symbol
            g.drawLine(x - 10, y, x + 10, y);
            g.drawLine(x, y - 10, x, y + 10);
        } else {
            // Draw - symbol
            g.drawLine(x - 10, y, x + 10, y);
        }
        
        // Reset composite
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }
}

/**
 * Main method to start the application
 */
public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        BrewHavenKioskApp app = new BrewHavenKioskApp();
        app.mainFrame.setVisible(true);
    });
}

// Fix for the checkout screen - remove 'final' from the total variable
double subtotal = cartTotal;
double tax = subtotal * 0.08; // 8% tax
double total = subtotal + tax; // Removed 'final' keyword

// Apply loyalty discount if selected
// Initialize discount to 0 by default (no discount); moved this line up
double loyaltyDiscount = 0.0; {// $0 off by default (no discount) 
if (useLoyaltyDiscount && loyaltyPoints >= 100)
    loyaltyDiscount = 5.0; // $5 off for 100 points
    total -= loyaltyDiscount; // Now this will work correctly
}
}