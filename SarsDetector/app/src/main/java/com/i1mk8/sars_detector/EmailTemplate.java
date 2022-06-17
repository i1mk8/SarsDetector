package com.i1mk8.sars_detector;

public class EmailTemplate {
    static String getTemplate(Double[][] breathing) {
        String template = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
                "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                "<head>\n" +
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n" +
                "<title>Детектор ОРВИ</title>\n" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>\n" +
                "<script src=\"https://cdn.plot.ly/plotly-2.12.1.min.js\"></script>\n" +
                "</head>\n" +
                "<body>\n" +
                "<div id=\"breathing\">\n" +
                "</div>\n" +
                "<script>\n" +
                "var yArray = [";

        for (Double[] item : breathing) {
            template += item[1] + ",";
        }
        template += "];\nvar xArray = [";
        for (Double[] item : breathing) {
            template += item[0] + ",";
        }

        template += "];\n" +
                "var data = [{" +
                "x: xArray," +
                "y: yArray," +
                "mode: \"lines\"," +
                "type: \"scatter\"}];" +
                "var layout = {" +
                "xaxis: {title: \"Время\"}," +
                "yaxis: {title: \"Громкость\"}," +
                "title: \"Дыхание\"};\n" +
                "Plotly.newPlot(\"breathing\", data, layout);\n" +
                "</script>\n" +
                "</body>\n" +
                "</html>";

        return template;
    }
}
