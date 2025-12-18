package com.ldr.andon;

public class OeeSummary {

    private double availability;  // Disponibilidad
    private double performance;   // Rendimiento
    private double quality;       // Calidad
    private double oee;           // OEE global

    public double getAvailability() {
        return availability;
    }

    public void setAvailability(double availability) {
        this.availability = availability;
    }

    public double getPerformance() {
        return performance;
    }

    public void setPerformance(double performance) {
        this.performance = performance;
    }

    public double getQuality() {
        return quality;
    }

    public void setQuality(double quality) {
        this.quality = quality;
    }

    public double getOee() {
        return oee;
    }

    public void setOee(double oee) {
        this.oee = oee;
    }
}
