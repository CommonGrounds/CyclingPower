package dev.java4now.web.util;

import dev.webfx.platform.console.Console;

public class SustainabilityCalculator {

    // Konstante za proračun
    private static final double KCAL_PER_LITER_GASOLINE = 7650.0;
    private static final double AVG_CAR_EFFICIENCY = 0.20; // 20% efikasnost motora sa unutrašnjim sagorevanjem
    private static final double CO2_KG_PER_LITER = 2.31;   // Koliko kg CO2 nastaje sagorevanjem 1L benzina

    /**
     * Pretvara potrošene kalorije sa bicikla u ekvivalent litara benzina
     * koje bi automobil potrošio za isti mehanički rad.
     */
    public static double caloriesToGasolineLiters(double burnedCalories) {
        // Izračunavamo koliko bi goriva automobil morao da sagori
        // da bi proizveo istu energiju, uzimajući u obzir gubitke toplote motora.
        return (burnedCalories / KCAL_PER_LITER_GASOLINE) / AVG_CAR_EFFICIENCY;
    }

    /**
     * Proračun smanjenja karbonskog otiska (u kilogramima CO2).
     */
    public static double calculateCo2Saved(double gasolineLiters) {
        return gasolineLiters * CO2_KG_PER_LITER;
    }


    public static String getHtml(Double calories) {
        double myCalories = calories;

        double litersSaved = caloriesToGasolineLiters(myCalories);
        double co2Saved = calculateCo2Saved(litersSaved);

        Console.log("Za utrošenih " + Format.formatDouble_GWT(myCalories,2) + " kcal");
        Console.log("- Uštedeli ste: " + Format.formatDouble_GWT(litersSaved,3) + " litara benzina");
        Console.log("- Smanjili ste karbonski otisak za: "  + Format.formatDouble_GWT(co2Saved,3) + " kg CO2");

        // <span style='white-space: nowrap;'> // important kada treba u jednom redu
        return
                "<div style=\"font-family:'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; " +
                        "max-width:400px; padding:20px; background:linear-gradient(135deg, #f0f9f1, #ffffff); " +
                        "border-radius:18px; box-shadow:0 10px 30px rgba(0,0,0,0.1); color:#2d3436; " +
                        "border:1px solid #e0e0e0; margin:0 auto;\"> " +   // dodato margin:0 auto; za centriranje

                        "<style>" +
                        "  @media (max-width: 1500px) { " +
                        "    div.card { " +                     // koristimo klasu umesto inline stilova gde god možemo
                        "      padding: 0px !important; " +
                        "    } " +
                        "    h2 { font-size: 1.3em !important; margin-bottom: 10px !important; } " +
                        "    .stat-row { font-size: 0.85em !important; } " +
                        "    .badge { " +
                        "      padding: 6px 14px !important; " +
                        "      font-size: 0.8em !important; " +
                        "    } " +
                        "    p.note { font-size: 0.7em !important; } " +
                        "  } " +
                        "  @media (max-width: 1200px) { " +
                        "    div.card { " +                     // koristimo klasu umesto inline stilova gde god možemo
                        "      padding: 0px !important; " +
                        "    } " +
                        "    h2 { font-size: 1.1em !important; margin-bottom: 10px !important; } " +
                        "    .stat-row { font-size: 0.7em !important; } " +
                        "    .badge { " +
                        "      padding: 5px 11px !important; " +
                        "      font-size: 0.65em !important; " +
                        "    } " +
                        "    p.note { font-size: 0.6em !important; } " +
                        "  } " +
                        "  @media (max-width: 1000px) { " +   // vrlo mali ekrani (stari telefoni)
                        "    h2 { font-size: 0.9em !important; } " +
                        "    .stat-row  { font-size: 0.6em !important; }  " +
                        "    .badge { padding: 4px 8px !important; font-size: 0.6em !important; } " +
                        " p.note { font-size: 0.5em !important; }  " +
                        "  } " +
                        "</style>" +

                        "<div class=\"card\">" +   // dodajemo klasu card da bismo lakše targetovali u @media
                        " <h2 style=\"text-align:center; color:#27ae60; font-size:1.5em; margin:0 0 15px; " +
                        "   text-shadow: 1px 1px 2px rgba(0,0,0,0.05);\">🌍 Eco-Mission Status</h2>" +

                        " <div style=\"background:rgba(39, 174, 96, 0.05); border-radius:12px; padding:15px; " +
                        "     border-left:5px solid #2ecc71;\">" +

                        "   <div class=\"stat-row\" style=\"display:flex; justify-content:space-between; " +
                        "       margin-bottom:10px; border-bottom:1px solid rgba(0,0,0,0.05); padding-bottom:5px;\">" +
                        "     <span>🔥 Energy Burned</span>" +
                        "     <strong style=\"color:#e67e22;\">" + Format.formatDouble_GWT(myCalories,0) + " kcal</strong>" +
                        "   </div>" +

                        "   <div class=\"stat-row\" style=\"display:flex; justify-content:space-between; " +
                        "       margin-bottom:10px; border-bottom:1px solid rgba(0,0,0,0.05); padding-bottom:5px;\">" +
                        "     <span>⛽ Fuel Saved</span>" +
                        "     <strong style=\"color:#2980b9;\">" + Format.formatDouble_GWT(litersSaved,2) + " L</strong>" +
                        "   </div>" +

                        "   <div class=\"stat-row\" style=\"display:flex; justify-content:space-between;\">" +
                        "     <span>🌱 CO2 Offset</span>" +
                        "     <strong style=\"color:#27ae60; font-weight:bold;\">" +
                        Format.formatDouble_GWT(co2Saved,2) + " kg</strong>" +
                        "   </div>" +
                        " </div>" +

                        " <div style=\"text-align:center; margin-top:20px;\">" +
                        "   <div class=\"badge\" style=\"display:inline-block; background:#2ecc71; color:#ffffff; " +
                        "       padding:8px 20px; border-radius:25px; font-weight:bold; font-size:0.9em; " +
                        "       box-shadow:0 4px 15px rgba(46,204,113,0.3);\">" +
                        "     🚲 Cycling vs. Oil Crisis: 1-0" +
                        "   </div>" +
                        " </div>" +

                        " <p class=\"note\" style=\"font-size:0.75em; color:#95a5a6; text-align:center; " +
                        "     margin-top:12px; font-style:italic;\">" +
                        "   Every pedal stroke bypasses the Strait of Hormuz!" +
                        " </p>" +
                        "</div>" +
                        "</div>";
    }
}
