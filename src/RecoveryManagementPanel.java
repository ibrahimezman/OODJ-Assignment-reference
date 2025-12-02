import domain.Milestone;
import domain.RecoveryPlan;
import service.FailedStudent;
import service.RecoveryFileManager;
import service.RecoveryService;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * RecoveryManagementPanel is a GUI panel that allows Academic Officers to manage
 * recovery plans for students who need academic recovery.
 * 
 * This panel provides:
 * - A list of students needing recovery (from RecoveryService)
 * - An input form to create recovery plans with milestones
 * - Functionality to save recovery plans to disk
 * 
 * Layout:
 * - Left side: JList displaying students needing recovery
 * - Right side: Form for creating recovery plans with milestones
 */
public class RecoveryManagementPanel extends JPanel
{
    // Left side components - Student List
    private JList<FailedStudent> studentList;
    private DefaultListModel<FailedStudent> studentListModel;
    
    // Right side components - Input Form
    private JTextField studentIdField;
    private JTextField courseIdField;
    private JTextArea recommendationArea;
    
    // Milestone section components
    private JTextField weekField;
    private JTextField taskField;
    private JButton addMilestoneButton;
    private JTextArea milestonesDisplayArea;
    
    // Save button
    private JButton saveButton;
    
    // Service classes for data access
    private RecoveryService recoveryService;
    private RecoveryFileManager recoveryFileManager;
    
    // Temporary storage for milestones being built
    private ArrayList<Milestone> tempMilestones;

    /**
     * Constructor - Initializes the panel with all its components.
     * Sets up the layout with a student list on the left and an input form on the right.
     */
    public RecoveryManagementPanel()
    {
        // Initialize services and temporary milestone storage
        recoveryService = new RecoveryService();
        recoveryFileManager = new RecoveryFileManager();
        tempMilestones = new ArrayList<>();
        
        // Set up the main layout using BorderLayout
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create and add the left panel (student list)
        JPanel leftPanel = createStudentListPanel();
        add(leftPanel, BorderLayout.WEST);
        
        // Create and add the right panel (input form)
        JPanel rightPanel = createInputFormPanel();
        add(rightPanel, BorderLayout.CENTER);
        
        // Load students into the list
        loadStudents();
    }

    /**
     * Creates the left panel containing the list of students needing recovery.
     * The list displays students returned by RecoveryService.getStudentsNeedingRecovery().
     * 
     * @return JPanel containing the student list
     */
    private JPanel createStudentListPanel()
    {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), 
            "Students Needing Recovery",
            TitledBorder.LEFT,
            TitledBorder.TOP
        ));
        panel.setPreferredSize(new Dimension(300, 400));
        
        // Create the list model and JList
        studentListModel = new DefaultListModel<>();
        studentList = new JList<>(studentListModel);
        studentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Custom cell renderer to display student info in a readable format
        studentList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof FailedStudent) {
                    FailedStudent student = (FailedStudent) value;
                    setText(student.getStudentId() + " - " + student.getCourseId() + 
                           " (" + student.getFailedComponent() + ")");
                }
                return this;
            }
        });
        
        /*
         * ListSelectionListener Explanation:
         * 
         * A ListSelectionListener is an interface that listens for selection changes in a JList.
         * When the user clicks on a different item in the list, the valueChanged() method is called.
         * 
         * We use this to:
         * 1. Detect when a student is selected in the list
         * 2. Auto-fill the StudentID and CourseID fields with the selected student's information
         * 3. Clear the previous form data to prepare for a new recovery plan
         * 
         * The getValueIsAdjusting() check prevents the listener from firing multiple times
         * during a single selection change (e.g., when using keyboard navigation).
         */
        studentList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                // Only process the event when the selection has finished adjusting
                if (!e.getValueIsAdjusting()) {
                    FailedStudent selected = studentList.getSelectedValue();
                    if (selected != null) {
                        // Auto-fill the read-only fields with student data
                        studentIdField.setText(selected.getStudentId());
                        courseIdField.setText(selected.getCourseId());
                        
                        // Clear the form for a new recovery plan
                        clearFormFields();
                    }
                }
            }
        });
        
        // Add the list to a scroll pane
        JScrollPane scrollPane = new JScrollPane(studentList);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Add a refresh button at the bottom
        JButton refreshButton = new JButton("Refresh List");
        
        /*
         * ActionListener Explanation for Refresh Button:
         * 
         * An ActionListener is an interface that listens for action events, typically
         * triggered by user interactions like button clicks.
         * 
         * When the user clicks the "Refresh List" button:
         * 1. The actionPerformed() method is called automatically by Swing
         * 2. We reload the list of students needing recovery from the service
         * 
         * This allows the Academic Officer to see updated data after changes are made.
         */
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadStudents();
            }
        });
        panel.add(refreshButton, BorderLayout.SOUTH);
        
        return panel;
    }

    /**
     * Creates the right panel containing the input form for creating recovery plans.
     * Includes fields for StudentID, CourseID, Recommendation, and Milestones.
     * 
     * @return JPanel containing the input form
     */
    private JPanel createInputFormPanel()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), 
            "Create Recovery Plan",
            TitledBorder.LEFT,
            TitledBorder.TOP
        ));
        
        // Student ID field (read-only)
        JPanel studentIdPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        studentIdPanel.add(new JLabel("Student ID:"));
        studentIdField = new JTextField(15);
        studentIdField.setEditable(false); // Read-only as per requirements
        studentIdField.setBackground(new Color(240, 240, 240)); // Gray background to indicate read-only
        studentIdPanel.add(studentIdField);
        panel.add(studentIdPanel);
        
        // Course ID field (read-only)
        JPanel courseIdPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        courseIdPanel.add(new JLabel("Course ID:"));
        courseIdField = new JTextField(15);
        courseIdField.setEditable(false); // Read-only as per requirements
        courseIdField.setBackground(new Color(240, 240, 240)); // Gray background to indicate read-only
        courseIdPanel.add(courseIdField);
        panel.add(courseIdPanel);
        
        // Recommendation text area
        JPanel recommendationPanel = new JPanel(new BorderLayout());
        recommendationPanel.setBorder(BorderFactory.createTitledBorder("Recommendation"));
        recommendationArea = new JTextArea(4, 30);
        recommendationArea.setLineWrap(true);
        recommendationArea.setWrapStyleWord(true);
        JScrollPane recScrollPane = new JScrollPane(recommendationArea);
        recommendationPanel.add(recScrollPane, BorderLayout.CENTER);
        panel.add(recommendationPanel);
        
        // Milestone section
        JPanel milestoneSection = createMilestoneSection();
        panel.add(milestoneSection);
        
        // Save button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        saveButton = new JButton("Save Recovery Plan");
        saveButton.setFont(new Font("Arial", Font.BOLD, 14));
        saveButton.setPreferredSize(new Dimension(200, 40));
        
        /*
         * ActionListener Explanation for Save Button:
         * 
         * An ActionListener is an interface that listens for action events from UI components.
         * When the user clicks the "Save Recovery Plan" button:
         * 
         * 1. The actionPerformed() method is automatically invoked by Swing's event dispatch thread
         * 2. We validate that a student is selected and the form has required data
         * 3. We create a new RecoveryPlan object with the input data and milestones
         * 4. We call RecoveryFileManager.savePlan() to persist the plan to disk
         * 5. We show a JOptionPane success/error message to provide feedback to the user
         * 
         * The ActionListener pattern allows us to separate the UI components from
         * the business logic, making the code more maintainable and testable.
         */
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveRecoveryPlan();
            }
        });
        buttonPanel.add(saveButton);
        panel.add(buttonPanel);
        
        return panel;
    }

    /**
     * Creates the milestone section of the form.
     * Includes text fields for Week and Task, an Add Milestone button,
     * and a display area showing milestones being built.
     * 
     * @return JPanel containing the milestone section
     */
    private JPanel createMilestoneSection()
    {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBorder(BorderFactory.createTitledBorder("Milestones"));
        
        // Week and Task input fields
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.add(new JLabel("Week:"));
        weekField = new JTextField(10);
        inputPanel.add(weekField);
        
        inputPanel.add(new JLabel("Task:"));
        taskField = new JTextField(20);
        inputPanel.add(taskField);
        
        // Add Milestone button
        addMilestoneButton = new JButton("Add Milestone");
        
        /*
         * ActionListener Explanation for Add Milestone Button:
         * 
         * An ActionListener is an interface with a single method: actionPerformed(ActionEvent e).
         * When the button is clicked, Swing's event system detects this and calls actionPerformed().
         * 
         * How it works:
         * 1. User clicks the "Add Milestone" button
         * 2. Swing creates an ActionEvent object containing information about the click
         * 3. Swing calls the actionPerformed() method we defined, passing the ActionEvent
         * 4. Our code inside actionPerformed() executes:
         *    - Validates that Week and Task fields are not empty
         *    - Creates a new Milestone object with the input data
         *    - Adds the milestone to our temporary list (tempMilestones)
         *    - Updates the display area to show all milestones
         *    - Clears the input fields for the next milestone
         * 
         * This allows the Academic Officer to build a list of milestones before saving
         * the complete recovery plan.
         */
        addMilestoneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addMilestone();
            }
        });
        inputPanel.add(addMilestoneButton);
        section.add(inputPanel);
        
        // Milestones display area - shows the list of milestones being built
        JPanel displayPanel = new JPanel(new BorderLayout());
        displayPanel.setBorder(BorderFactory.createTitledBorder("Milestones Added"));
        milestonesDisplayArea = new JTextArea(5, 30);
        milestonesDisplayArea.setEditable(false); // Read-only display
        milestonesDisplayArea.setBackground(new Color(250, 250, 250));
        JScrollPane milestoneScrollPane = new JScrollPane(milestonesDisplayArea);
        displayPanel.add(milestoneScrollPane, BorderLayout.CENTER);
        
        // Clear milestones button
        JButton clearMilestonesButton = new JButton("Clear Milestones");
        
        /*
         * ActionListener Explanation for Clear Milestones Button:
         * 
         * This ActionListener follows the same pattern as the others.
         * When clicked:
         * 1. Clear the temporary milestones list
         * 2. Clear the display area
         * 
         * This allows the user to start over if they made a mistake.
         */
        clearMilestonesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tempMilestones.clear();
                milestonesDisplayArea.setText("");
            }
        });
        displayPanel.add(clearMilestonesButton, BorderLayout.SOUTH);
        section.add(displayPanel);
        
        return section;
    }

    /**
     * Loads students needing recovery from the RecoveryService and populates the list.
     */
    private void loadStudents()
    {
        studentListModel.clear();
        ArrayList<FailedStudent> students = recoveryService.getStudentsNeedingRecovery();
        for (FailedStudent student : students) {
            studentListModel.addElement(student);
        }
    }

    /**
     * Adds a milestone to the temporary list and updates the display area.
     * Called when the "Add Milestone" button is clicked.
     */
    private void addMilestone()
    {
        String week = weekField.getText().trim();
        String task = taskField.getText().trim();
        
        // Validate input
        if (week.isEmpty() || task.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter both Week and Task for the milestone.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Create a new milestone with "Pending" status and add to temporary list
        Milestone milestone = new Milestone(week, task, "Pending");
        tempMilestones.add(milestone);
        
        // Update the display area
        updateMilestonesDisplay();
        
        // Clear the input fields for the next milestone
        weekField.setText("");
        taskField.setText("");
        weekField.requestFocus();
    }

    /**
     * Updates the milestones display area to show all milestones in the temporary list.
     */
    private void updateMilestonesDisplay()
    {
        StringBuilder sb = new StringBuilder();
        int count = 1;
        for (Milestone m : tempMilestones) {
            sb.append(count).append(". ").append(m.getWeek())
              .append(": ").append(m.getTask()).append("\n");
            count++;
        }
        milestonesDisplayArea.setText(sb.toString());
    }

    /**
     * Clears the form fields (recommendation, milestones) for a new recovery plan.
     * Called when a new student is selected from the list.
     */
    private void clearFormFields()
    {
        recommendationArea.setText("");
        weekField.setText("");
        taskField.setText("");
        tempMilestones.clear();
        milestonesDisplayArea.setText("");
    }

    /**
     * Saves the recovery plan to disk using RecoveryFileManager.
     * Called when the "Save Recovery Plan" button is clicked.
     * 
     * This method:
     * 1. Validates that a student is selected
     * 2. Validates that recommendation is provided
     * 3. Creates a new RecoveryPlan object with all the input data
     * 4. Adds all milestones from the temporary list to the plan
     * 5. Calls RecoveryFileManager.savePlan() to write to disk
     * 6. Shows a success or error message using JOptionPane
     */
    private void saveRecoveryPlan()
    {
        // Validate that a student is selected
        String studentId = studentIdField.getText().trim();
        String courseId = courseIdField.getText().trim();
        
        if (studentId.isEmpty() || courseId.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please select a student from the list first.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get the recommendation
        String recommendation = recommendationArea.getText().trim();
        if (recommendation.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter a recommendation for the recovery plan.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Create the RecoveryPlan object with "Active" status
        RecoveryPlan plan = new RecoveryPlan(studentId, courseId, recommendation, "Active");
        
        // Add all milestones from the temporary list to the plan
        for (Milestone milestone : tempMilestones) {
            plan.addMilestone(milestone);
        }
        
        // Save the plan to disk using RecoveryFileManager
        boolean success = recoveryFileManager.savePlan(plan);
        
        if (success) {
            // Show success message
            JOptionPane.showMessageDialog(this,
                "Recovery plan saved successfully!\n\n" +
                "Student ID: " + studentId + "\n" +
                "Course ID: " + courseId + "\n" +
                "Milestones: " + tempMilestones.size(),
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
            
            // Clear the form after successful save
            clearFormFields();
        } else {
            // Show error message
            JOptionPane.showMessageDialog(this,
                "Failed to save recovery plan. Please try again.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Main method to run the panel standalone for testing.
     * Creates a JFrame and adds the RecoveryManagementPanel to it.
     */
    public static void main(String[] args)
    {
        // Use SwingUtilities.invokeLater to ensure thread safety
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Recovery Plan Management");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(800, 600);
                frame.setLocationRelativeTo(null); // Center on screen
                
                RecoveryManagementPanel panel = new RecoveryManagementPanel();
                frame.add(panel);
                
                frame.setVisible(true);
            }
        });
    }
}
