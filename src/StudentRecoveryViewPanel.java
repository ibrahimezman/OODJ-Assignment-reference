import domain.Milestone;
import domain.RecoveryPlan;
import service.RecoveryFileManager;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * StudentRecoveryViewPanel is a GUI panel that allows a student to view their assigned recovery plan.
 * 
 * This panel:
 * - Accepts a studentId in the constructor to identify the logged-in student
 * - Fetches all recovery plans using RecoveryFileManager.loadPlans()
 * - Filters the plans to find the one matching the current studentId
 * - Displays the plan's Recommendation and Course information in Labels
 * - Shows the Milestones in a JTable with columns: Week, Task, Status
 * - Shows an empty state message if no plan is found for this student
 * 
 * Layout:
 * - Top section: Labels showing Course and Recommendation (if plan exists)
 * - Center section: JTable displaying milestones
 * - Empty state: Large centered label when no plan is found
 */
public class StudentRecoveryViewPanel extends JPanel
{
    // The ID of the currently logged-in student
    private String studentId;
    
    // The recovery plan for this student (null if none exists)
    private RecoveryPlan studentPlan;
    
    // Service class for loading recovery plans from file
    private RecoveryFileManager recoveryFileManager;
    
    // UI components for displaying plan information
    private JLabel courseLabel;
    private JLabel recommendationLabel;
    private JTable milestonesTable;
    private DefaultTableModel tableModel;
    
    // Empty state label (shown when no plan is found)
    private JLabel emptyStateLabel;

    /**
     * Constructor - Initializes the panel for a specific student.
     * 
     * @param studentId The unique identifier of the logged-in student (e.g., "S001")
     */
    public StudentRecoveryViewPanel(String studentId)
    {
        this.studentId = studentId;
        this.recoveryFileManager = new RecoveryFileManager();
        
        // Set up the main layout
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Fetch the recovery plan for this student
        fetchStudentPlan();
        
        // Build the UI based on whether a plan was found
        if (studentPlan != null)
        {
            // Plan found - show the plan details
            buildPlanView();
        }
        else
        {
            // No plan found - show the empty state
            buildEmptyState();
        }
    }

    /**
     * Fetches the recovery plan for the current student.
     * 
     * Filtering Logic:
     * 1. Load all recovery plans from the file using RecoveryFileManager.loadPlans()
     * 2. Loop through each plan in the returned list
     * 3. Compare each plan's studentId with our current studentId
     * 4. If a match is found, store that plan and stop searching
     * 5. If no match is found, studentPlan remains null
     * 
     * Note: This assumes one plan per student. If multiple plans exist for the same
     * student, only the first one found will be used.
     */
    private void fetchStudentPlan()
    {
        // Load all recovery plans from the file
        ArrayList<RecoveryPlan> allPlans = recoveryFileManager.loadPlans();
        
        // Reset studentPlan to null before searching
        studentPlan = null;
        
        /*
         * FILTERING LOGIC:
         * Iterate through all loaded plans and find the one matching this student's ID.
         * We use a simple for-each loop to check each plan's studentId against
         * the studentId passed to this panel's constructor.
         */
        for (RecoveryPlan plan : allPlans)
        {
            // Compare the plan's student ID with our logged-in student ID
            if (plan.getStudentId().equals(studentId))
            {
                // Found a matching plan - store it and exit the loop
                studentPlan = plan;
                break;
            }
        }
        
        // Log the result for debugging purposes
        if (studentPlan != null)
        {
            System.out.println("Found recovery plan for student: " + studentId);
        }
        else
        {
            System.out.println("No recovery plan found for student: " + studentId);
        }
    }

    /**
     * Builds the UI to display the recovery plan details.
     * Called when a plan is found for the student.
     * 
     * Layout:
     * - NORTH: Panel with Course and Recommendation labels
     * - CENTER: JTable with milestones (Week, Task, Status columns)
     */
    private void buildPlanView()
    {
        // Create the top panel for Course and Recommendation information
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Recovery Plan Details",
            TitledBorder.LEFT,
            TitledBorder.TOP
        ));
        
        // Course label
        JPanel coursePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        coursePanel.add(new JLabel("Course: "));
        courseLabel = new JLabel(studentPlan.getCourseId());
        courseLabel.setFont(new Font("Arial", Font.BOLD, 14));
        coursePanel.add(courseLabel);
        infoPanel.add(coursePanel);
        
        // Recommendation label (with word wrap for long text)
        JPanel recPanel = new JPanel(new BorderLayout());
        recPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JLabel recTitleLabel = new JLabel("Recommendation: ");
        recPanel.add(recTitleLabel, BorderLayout.NORTH);
        
        recommendationLabel = new JLabel("<html><body style='width: 400px'>" + 
            studentPlan.getRecommendation() + "</body></html>");
        recommendationLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        recPanel.add(recommendationLabel, BorderLayout.CENTER);
        infoPanel.add(recPanel);
        
        add(infoPanel, BorderLayout.NORTH);
        
        // Create the milestones table
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Milestones",
            TitledBorder.LEFT,
            TitledBorder.TOP
        ));
        
        // Set up the table model with three columns: Week, Task, Status
        String[] columnNames = {"Week", "Task", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0)
        {
            // Make the table cells non-editable (read-only view)
            @Override
            public boolean isCellEditable(int row, int column)
            {
                return false;
            }
        };
        
        // Populate the table with milestones from the plan
        List<Milestone> milestones = studentPlan.getMilestones();
        for (Milestone milestone : milestones)
        {
            Object[] rowData = {
                milestone.getWeek(),
                milestone.getTask(),
                milestone.getStatus()
            };
            tableModel.addRow(rowData);
        }
        
        // Create the JTable and add it to a scroll pane
        milestonesTable = new JTable(tableModel);
        milestonesTable.setRowHeight(25);
        milestonesTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        milestonesTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // Week
        milestonesTable.getColumnModel().getColumn(1).setPreferredWidth(300); // Task
        milestonesTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Status
        
        JScrollPane scrollPane = new JScrollPane(milestonesTable);
        scrollPane.setPreferredSize(new Dimension(500, 200));
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        add(tablePanel, BorderLayout.CENTER);
    }

    /**
     * Builds the empty state UI when no recovery plan is found for the student.
     * Displays a large, centered message informing the student.
     */
    private void buildEmptyState()
    {
        // Create a centered label with the empty state message
        emptyStateLabel = new JLabel("No active recovery plans assigned.", SwingConstants.CENTER);
        emptyStateLabel.setFont(new Font("Arial", Font.BOLD, 20));
        emptyStateLabel.setForeground(Color.GRAY);
        
        // Add the label to the center of the panel
        add(emptyStateLabel, BorderLayout.CENTER);
    }

    /**
     * Gets the student ID this panel is displaying data for.
     * 
     * @return The student ID
     */
    public String getStudentId()
    {
        return studentId;
    }

    /**
     * Gets the recovery plan being displayed (may be null if no plan exists).
     * 
     * @return The RecoveryPlan object, or null if no plan was found
     */
    public RecoveryPlan getStudentPlan()
    {
        return studentPlan;
    }

    /**
     * Main method to run the panel standalone for testing.
     * Creates a JFrame and adds the StudentRecoveryViewPanel to it.
     * 
     * Usage:
     * - Pass a student ID as a command line argument
     * - Or run without arguments to use a default test ID
     */
    public static void main(String[] args)
    {
        // Use SwingUtilities.invokeLater to ensure thread safety
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                // Default student ID for testing - can be overridden via command line
                String testStudentId = args.length > 0 ? args[0] : "S001";
                
                JFrame frame = new JFrame("My Recovery Plan - Student View");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(600, 500);
                frame.setLocationRelativeTo(null); // Center on screen
                
                StudentRecoveryViewPanel panel = new StudentRecoveryViewPanel(testStudentId);
                frame.add(panel);
                
                frame.setVisible(true);
            }
        });
    }
}
