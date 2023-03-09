package org.matsim.project.run;

public final class DMCCoeficients {
    public static final double privateCAR = (0.885 - 0.253); // private costs of car (Gössling 2019) minus the cost of travel time per km
    public static final double constantCAR = 0;
    public static final double socialGASOLINE = 0;
    public static final double socialBEV = 0;
    public static final double socialDIESEL = 0;
    public static final double socialPHEV = 0;
    public static final double votCAR = 4.63; //VOT of car driving (Moeckel) EUR/h

    public static final double privatePT = (63.2/22)/3.2; //private cost of public transport: MVG Monthly ticket / 22 days / 3.2 trips
    public static final double constantPT = 0.0;
    public static final double socialBUS = 0;
    public static final double socialTRAM = 0;
    public static final double socialSUBWAY = 0;
    public static final double votPT = 8.94; //VOT of pt (Moeckel) EUR/h

    public static final double privateBIKE = 0.147; //private cost of cycling (Gössling 2019)
    public static final double constantBIKE = 0; //Calibration value
    public static final double socialBIKE = 0;

    public static final double privateWALK = 0.449; //private cost of walking (Gössling 2019)
    public static final double constantWALK = 1; //Calibration value
    public static final double socialWALK = 0;
}
