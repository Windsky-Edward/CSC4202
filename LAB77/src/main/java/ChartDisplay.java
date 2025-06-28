import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.util.Map;

public class ChartDisplay {
    public static void showBarChart(String title, Map<String, Double> data, String unit) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            dataset.addValue(entry.getValue(), "Time", entry.getKey());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                title, "Algorithm", unit, dataset,
                PlotOrientation.VERTICAL, false, true, false
        );

        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setItemMargin(0.4);
        renderer.setMaximumBarWidth(0.05);

        ChartPanel panel = new ChartPanel(chart);
        JFrame frame = new JFrame(title);
        frame.setContentPane(panel);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
