import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.awt.Point;

public class WeatherAppUI {

    // --- NUEVA PALETA DE COLORES "ANIME SKY" (Pastel) ---
    // Usamos el estado is_day para cambiar el fondo
    private static final Color COLOR_FONDO_DIA = new Color(135, 206, 250); // LightSkyBlue (Cielo de día anime)
    private static final Color COLOR_FONDO_NOCHE = new Color(44, 62, 80); // Midnight Blue (Cielo de noche)
    private static final Color TEXTO_PRINCIPAL = new Color(255, 255, 255); // Blanco puro para buen contraste
    private static final Color TEXTO_SECUNDARIO = new Color(236, 240, 241); // Blanco roto suave
    private static final Color FONDO_TARJETA = new Color(255, 255, 255, 100); // Blanco con transparencia (Toque cristal suave)

    public static void main(String[] args) {
        // Tu lógica original de datos se mantiene intacta
        Modelo.mapInfo(Modelo.traerInfo());
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("App de Clima Minimalista");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1050, 600); // Un poco más ancho para que los textos no se corten

        // Redondeamos la ventana
        frame.setUndecorated(true);
        frame.setShape(new java.awt.geom.RoundRectangle2D.Double(0, 0, 1050, 600, 30, 30));

        // 1. Calculamos el color (Día o Noche)
        Color colorPrincipal = Elementos.is_day.equals("☀️") ? COLOR_FONDO_DIA : COLOR_FONDO_NOCHE;

        // 2. EL TRUCO: Creamos un Panel Maestro
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new GridLayout(1, 3, 20, 0)); // Redujimos el espacio entre columnas a 20
        panelPrincipal.setBackground(colorPrincipal); // Pintamos ESTE panel, no la ventana
        
        // El margen se lo aplicamos al Panel Maestro, no al RootPane
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40)); 

        // 3. Añadimos tus 3 paneles al Panel Maestro
        panelPrincipal.add(crearPanelIzquierdo());
        panelPrincipal.add(crearPanelCentral());
        panelPrincipal.add(crearPanelDerecho());

        // 4. Establecemos el Panel Maestro como el contenido total de la ventana
        frame.setContentPane(panelPrincipal);
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("ponce9/casa.png")));
        // Listener para poder mover la ventana con el mouse
        MoverVentanaListener moverListener = new MoverVentanaListener(frame);
        frame.addMouseListener(moverListener);
        frame.addMouseMotionListener(moverListener);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // --- PANEL IZQUIERDO (Hero Section) ---
    private static JPanel crearPanelIzquierdo() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false); // Transparente para ver el fondo del frame

        // Tu lógica original de variables se mantiene
        JLabel lblUbicacion = crearLabel("📍Cartagena, Colombia", 22, true, TEXTO_PRINCIPAL); // Solo la ciudad
        lblUbicacion.setBorder(new EmptyBorder(0, 0, 0, 0)); // Reseteamos bordes
        
        JLabel lblFecha = crearLabel(Elementos.Date+"", 15, false, TEXTO_SECUNDARIO);
        
        JLabel lblHora = crearLabel("Cargando hora...", 15, true, TEXTO_SECUNDARIO);
        LocalTime ahora = LocalTime.parse(Elementos.hora);
        
        Timer timer = new Timer(1000, new ActionListener() {
            LocalTime ahoramasuno = ahora;
            @Override
            public void actionPerformed(ActionEvent e) {
                ahoramasuno = ahoramasuno.plusSeconds(1);
                DateTimeFormatter formatoNormal = DateTimeFormatter.ofPattern("hh:mm:ss a");
                DateTimeFormatter formatoMilitar = DateTimeFormatter.ofPattern("HH:mm:ss");
                String textoHora = ahoramasuno.format(formatoNormal) + "  |  " + ahoramasuno.format(formatoMilitar) + " (24h)";
                lblHora.setText(textoHora);
            }
        });
        timer.start();

        // Emojis grandes y temperatura gigante
        JLabel lblIcono = crearLabel(Elementos.is_day+Elementos.cloud, 70, false, TEXTO_PRINCIPAL); 
        JLabel lblTemp = crearLabel(Elementos.temp_c+"°C", 110, true, TEXTO_PRINCIPAL); // Más grande
        JLabel lblDesc = crearLabel(Elementos.text, 20, false, TEXTO_PRINCIPAL);
        JLabel lblMaxMin = crearLabel("Máx: "+Elementos.maxtemp_c[0]+"° | Min: "+Elementos.mintemp_c[0]+"°", 15, false, TEXTO_SECUNDARIO);

        // Alineamos todo a la izquierda y agregamos espaciadores
        panel.add(lblUbicacion);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(lblFecha);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(lblHora);
        panel.add(Box.createRigidArea(new Dimension(0, 40))); // Espacio grande antes del icono
        panel.add(lblIcono);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(lblTemp);
        panel.add(lblDesc);
        panel.add(lblMaxMin);

        return panel;
    }

    // --- PANEL CENTRAL (Detalles con Cajas Redondeadas) ---
    private static JPanel crearPanelCentral() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false); // Transparente

        JLabel titulo = crearLabel("Detalles Actuales", 20, true, TEXTO_PRINCIPAL);
        panel.add(titulo);
        panel.add(Box.createRigidArea(new Dimension(0, 25))); // Espacio

        // ¡Aquí está la magia! Usamos la clase auxiliar "PanelRedondeado" que creé abajo
        panel.add(new PanelRedondeado("💧 Humedad", Elementos.humidity+"%"));
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(new PanelRedondeado("💨༄ Viento", Elementos.wind_kph+" km/h "+ Elementos.wind_dir));
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(new PanelRedondeado("☀️ Índice UV", Elementos.getUv()+" ("+ Elementos.uv+")"));

        return panel;
    }

    // --- PANEL DERECHO (Pronóstico con Filas Redondeadas) ---
    private static JPanel crearPanelDerecho() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false); // Transparente

        JLabel titulo = crearLabel("Próximos Días", 20, true, TEXTO_PRINCIPAL);
        panel.add(titulo);
        panel.add(Box.createRigidArea(new Dimension(0, 25)));
        
        // Convertimos cada fila del pronóstico en una pequeña tarjeta redondeada
        for(int i=0; i<6; i++){
            // Tu lógica de variables original se mantiene
            panel.add(crearFilaPronosticoRedondeada(
                Elementos.diasSemana.get(i), 
                Elementos.daily_will_it_rain[i+1] == 1 ? "🌧️":"☀️", 
                Elementos.maxtemp_c[i+1]+"° / "+Elementos.mintemp_c[i+1]+"°"
            ));
            if(i < 5) panel.add(Box.createRigidArea(new Dimension(0, 10))); // Espacio entre días
        }
        
        return panel;
    }

    // --- MÉTODOS AUXILIARES ACTUALIZADOS ---

    private static JLabel crearLabel(String texto, int size, boolean bold, Color color) {
        JLabel label = new JLabel(texto);
        int style = bold ? Font.BOLD : Font.PLAIN;
        // Usamos una fuente SansSerif estándar que se ve moderna y limpia
        label.setFont(new Font("SansSerif", style, size));
        label.setForeground(color);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    // --- NUEVO COMPONENTE: TARJETA DE DETALLE REDONDEADA ---
    // Esta es una "clase auxiliar" que creé para lograr las esquinas redondeadas
    static class PanelRedondeado extends JPanel {
        private String tituloText;
        private String valorText;

        PanelRedondeado(String titulo, String valor) {
            this.tituloText = titulo;
            this.valorText = valor;
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            this.setBackground(FONDO_TARJETA); // Fondo semitransparente
            this.setBorder(new EmptyBorder(15, 15, 15, 15)); // Padding interno
            this.setAlignmentX(Component.LEFT_ALIGNMENT);
            this.setMaximumSize(new Dimension(300, 85)); // Un poco más grandes
            this.setOpaque(false); // Crucial: Java Swing dibuja bordes cuadrados por defecto

            this.add(crearLabel(tituloText, 15, false, TEXTO_SECUNDARIO));
            this.add(Box.createRigidArea(new Dimension(0, 8))); // Espacio entre título y valor
            this.add(crearLabel(valorText, 20, true, TEXTO_PRINCIPAL));
        }

        // ¡Aquí está el truco de Swing! Sobreescribimos el método de dibujo
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            // Activamos el suavizado de bordes (anti-aliasing)
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(getBackground());
            // Dibujamos un rectángulo relleno con bordes redondeados (radio 25px)
            g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 25, 25);
        }
    }

    // --- NUEVO COMPONENTE: FILA DE PRONÓSTICO REDONDEADA ---
    private static JPanel crearFilaPronosticoRedondeada(String dia, String icono, String temp) {
        // Usamos una subclase anónima de PanelRedondeadoFila para reutilizar la lógica de dibujo
        JPanel fila = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(FONDO_TARJETA);
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20); // Radio 20px
            }
        };
        fila.setOpaque(false); // Transparente para ver el borde redondeado
        fila.setBackground(FONDO_TARJETA);
        fila.setMaximumSize(new Dimension(350, 45)); // Más ancho y alto
        fila.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15)); // Padding interno

        // Reemplazamos crearLabel por crearLabelFila con texto blanco
        fila.add(crearLabel(dia, 16, false, TEXTO_PRINCIPAL), BorderLayout.WEST);
        
        // Centramos el icono
        JLabel lblIcono = crearLabel(icono, 16, false, TEXTO_PRINCIPAL);
        lblIcono.setHorizontalAlignment(SwingConstants.CENTER);
        fila.add(lblIcono, BorderLayout.CENTER);
        
        fila.add(crearLabel(temp, 16, true, TEXTO_PRINCIPAL), BorderLayout.EAST);

        return fila;
    }
    // --- NUEVA CLASE AUXILIAR: PERMITE ARRASTRAR LA VENTANA ---
    static class MoverVentanaListener extends java.awt.event.MouseAdapter {
        private final JFrame frame;
        private Point clickInicial;

        public MoverVentanaListener(JFrame frame) {
            this.frame = frame;
        }

        @Override
        public void mousePressed(java.awt.event.MouseEvent e) {
            // Guardamos dónde hizo clic el usuario inicialmente
            clickInicial = e.getPoint();
        }

        @Override
        public void mouseDragged(java.awt.event.MouseEvent e) {
            // Calculamos la nueva posición de la ventana restando el punto inicial
            int x = frame.getLocation().x + e.getX() - clickInicial.x;
            int y = frame.getLocation().y + e.getY() - clickInicial.y;
            frame.setLocation(x, y);
        }
    }
}