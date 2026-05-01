package ponce9;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.awt.Point;
import java.io.UnsupportedEncodingException;
import java.time.format.FormatStyle;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

public class WeatherAppUI {
    static JFrame frame = new JFrame("App de Clima Minimalista");

    // Usamos el estado is_day para cambiar el fondo
    private static final Color COLOR_FONDO_DIA = new Color(135, 206, 250); // LightSkyBlue (Cielo de día anime)
    private static final Color COLOR_FONDO_NOCHE = new Color(44, 62, 80); // Midnight Blue (Cielo de noche)
    private static final Color TEXTO_PRINCIPAL = new Color(255, 255, 255); // Blanco puro para buen contraste
    private static final Color TEXTO_SECUNDARIO = new Color(236, 240, 241); // Blanco roto suave
    private static final Color FONDO_TARJETA = new Color(255, 255, 255, 100); // Blanco con transparencia (Toque cristal suave)

    private static JLabel lblUbicacion; // Solo la ciudad;
    private static JLabel lblFecha;
    private static JLabel lblHora;
    private static JLabel lblIcono;
    private static JLabel lblTemp;
    private static JLabel lblDesc;
    private static JLabel lblMaxMin;
    private static Timer timer;
    private static JPanel panelPrincipal;
    private static JPanel panelCentro;
    private static JPanel panelContenido;
    private static JPanel barraSuperior;
    private static PanelRedondeado panelCentroHumedo;
    private static PanelRedondeado panelCentroOreado;
    private static PanelRedondeado panelCentroUv;
    private static PanelPronostico mañana;
    private static PanelPronostico pasadoMañana;
    private static JComboBox<String> comboCiudades;
    private static JButton botonGuardar;
    private static JLabel lblEstadoGuardado;
    private static String ultimaJson;
    private static String ciudadActual;
    private static final Map<String, String> ciudadesGuardadas = new LinkedHashMap<>();
    
    
    public static void main(String[] args) throws UnsupportedEncodingException {
        System.setProperty("java.home", ".");
        String json = Modelo.traerInfo("Cartagena","");
        ultimaJson = json;
        ciudadActual = "Cartagena";
        Modelo.mapInfo(json);
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1050, 600); // Un poco más ancho para que los textos no se corten

        // Redondeamos la ventana
        frame.setUndecorated(true);
        frame.setShape(new java.awt.geom.RoundRectangle2D.Double(0, 0, 1050, 600, 30, 30));

        // 1. Calculamos el color (Día o Noche)
        Color colorPrincipal = Elementos.is_day.equals("☀️") ? COLOR_FONDO_DIA : COLOR_FONDO_NOCHE;

        // 2. Panel Maestro con BorderLayout
        panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BorderLayout());
        panelPrincipal.setBackground(colorPrincipal);
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // 3. Barra superior (title bar) para arrastrar y cerrar
        JPanel barraSuperior = crearBarraSuperior(colorPrincipal);
        panelPrincipal.add(barraSuperior, BorderLayout.NORTH);

        // 4. Panel de contenido con los 3 paneles
        panelContenido = new JPanel();
        panelContenido.setLayout(new GridLayout(1, 3, 20, 0));
        panelContenido.setBackground(colorPrincipal);
        panelContenido.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        panelContenido.add(crearPanelIzquierdo());
        panelContenido.add(crearPanelCentral());
        panelContenido.add(crearPanelDerecho());
        panelPrincipal.add(panelContenido, BorderLayout.CENTER);

        // 5. Establecemos el Panel Maestro como el contenido total de la ventana
        frame.setContentPane(panelPrincipal);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // --- BARRA SUPERIOR (Title Bar para arrastrar y cerrar) ---
    private static JPanel crearBarraSuperior(Color colorFondo) {
        barraSuperior = new JPanel(new BorderLayout(10, 0));
        barraSuperior.setBackground(colorFondo);
        barraSuperior.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        barraSuperior.setPreferredSize(new Dimension(1050, 38));

        JLabel titulo = new JLabel("Climacarta");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 14));
        titulo.setForeground(TEXTO_PRINCIPAL);
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        barraSuperior.add(titulo, BorderLayout.WEST);

        JButton botonCerrar = new JButton("\u2715");
        botonCerrar.setFont(new Font("SansSerif", Font.BOLD, 14));
        botonCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botonCerrar.setBackground(new Color(80, 90, 105));
        botonCerrar.setForeground(Color.WHITE);
        botonCerrar.setFocusPainted(false);
        botonCerrar.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        botonCerrar.addActionListener(e -> System.exit(0));
        barraSuperior.add(botonCerrar, BorderLayout.EAST);

        MoverVentanaListener moverListener = new MoverVentanaListener(frame);
        barraSuperior.addMouseListener(moverListener);
        barraSuperior.addMouseMotionListener(moverListener);

        return barraSuperior;
    }

    // --- PANEL IZQUIERDO (Hero Section) ---
    private static JPanel crearPanelIzquierdo() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false); // Transparente para ver el fondo del frame

        // Tu lógica original de variables se mantiene
        lblUbicacion = crearLabel("📍"+Elementos.name+", "+Elementos.country, 22, true, TEXTO_PRINCIPAL);
        lblUbicacion.setBorder(new EmptyBorder(0, 0, 0, 0)); // Reseteamos bordes
        String fecha = Elementos.Date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).localizedBy(Locale.of("es")));
        lblFecha = crearLabel(fecha.substring(0,1).toUpperCase()+fecha.substring(1), 15, false, TEXTO_SECUNDARIO);
        
        lblHora = crearLabel("Cargando hora...", 15, true, TEXTO_SECUNDARIO);
        LocalTime ahora = Elementos.Date.toLocalTime();
        
        timer = new Timer(1000, new ActionListener() {
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
        lblIcono = crearLabel(Elementos.is_day+Elementos.cloud, 70, false, TEXTO_PRINCIPAL); 
        lblTemp = crearLabel(Elementos.temp_c+"°C", 110, true, TEXTO_PRINCIPAL); // Más grande
        lblDesc = crearLabel(Elementos.text, 20, false, TEXTO_PRINCIPAL);
        lblMaxMin = crearLabel("Máx: "+Elementos.maxtemp_c[0]+"° | Min: "+Elementos.mintemp_c[0]+"°", 15, false, TEXTO_SECUNDARIO);

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
        panelCentro = new JPanel();
        panelCentro.setLayout(new BoxLayout(panelCentro, BoxLayout.Y_AXIS));
        panelCentro.setOpaque(false); // Transparente

        JLabel titulo = crearLabel("Detalles Actuales", 20, true, TEXTO_PRINCIPAL);
        panelCentro.add(titulo);
        panelCentro.add(Box.createRigidArea(new Dimension(0, 25))); // Espacio
        panelCentroHumedo = new PanelRedondeado("💧 Humedad", Elementos.humidity+"%");
        panelCentroOreado = new PanelRedondeado("༄ Viento", Elementos.wind_kph+" km/h "+ Elementos.wind_dir);
        panelCentroUv = new PanelRedondeado("☀️ Índice UV", Elementos.getUv()+" ("+ Elementos.uv+")");
        // ¡Aquí está la magia! Usamos la clase auxiliar "PanelRedondeado" que creé abajo
        panelCentro.add(panelCentroHumedo);
        panelCentro.add(Box.createRigidArea(new Dimension(0, 20)));
        panelCentro.add(panelCentroOreado);
        panelCentro.add(Box.createRigidArea(new Dimension(0, 20)));
        panelCentro.add(panelCentroUv);

        return panelCentro;
    }

    // --- PANEL DERECHO (Pronóstico con Filas Redondeadas) ---
    private static JPanel crearPanelDerecho() {

        JPanel panelBusqueda = new JPanel(new BorderLayout(0,0));
        panelBusqueda.setOpaque(false);

        JTextField campoBusqueda = new JTextField(15);
        campoBusqueda.setPreferredSize(new Dimension(180,30));
        campoBusqueda.setMinimumSize(new Dimension(180,30));
        campoBusqueda.setMaximumSize(new Dimension(180,30));
        campoBusqueda.setFont(new Font("Arial", Font.PLAIN, 14));
        campoBusqueda.setToolTipText("Ej. Cartagena, Bogota, Madrid...");
        campoBusqueda.setBackground(new Color(80, 90, 105));
        campoBusqueda.setForeground(Color.WHITE);
        campoBusqueda.setCaretColor(Color.WHITE);
        campoBusqueda.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JButton botonBuscar = new JButton("Buscar");
        botonBuscar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botonBuscar.setBackground(new Color(80, 90, 105));
        botonBuscar.setForeground(Color.WHITE);
        botonBuscar.setFocusPainted(false);

        JPopupMenu menuResultados = new JPopupMenu();
        menuResultados.setBackground(FONDO_TARJETA);
        menuResultados.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 120)));

        ActionListener accionBuscar = (ActionEvent e) -> {
            String textoBuscado = campoBusqueda.getText().trim();
            if (!textoBuscado.isEmpty()) {
                botonBuscar.setText("...");
                botonBuscar.setEnabled(false);
                try {
                    menuResultados.removeAll();
                    List<DtoBusqueda> resultado = Modelo.buscarInfo(textoBuscado);
                    if (resultado.isEmpty()) {
                        javax.swing.JDialog dialogo = new javax.swing.JDialog();
                        dialogo.setTitle("Busqueda fallida");
                        dialogo.setModal(true);
                        dialogo.setSize(350, 150);
                        dialogo.setLocationRelativeTo(null);
                        dialogo.setLayout(new java.awt.BorderLayout());
                        javax.swing.JLabel etiquetaTexto = new javax.swing.JLabel("¡No se encontró ninguna ciudad!", javax.swing.SwingConstants.CENTER);
                        dialogo.add(etiquetaTexto, java.awt.BorderLayout.CENTER);
                        dialogo.setVisible(true);
                    } else if (resultado.size()==1) {
                        DtoBusqueda res = resultado.get(0);
                        String ciudadExacta = res.getName();
                        campoBusqueda.setText(ciudadExacta);
                        String json = Modelo.traerInfo(ciudadExacta,"");
                        ultimaJson = json;
                        ciudadActual = ciudadExacta;
                        Modelo.mapInfo(json);
                        actVentana();
                        lblEstadoGuardado.setText(" ");
                    } else {
                        for (DtoBusqueda res : resultado) {
                            JMenuItem item = new JMenuItem(res.getNombreCompleto());
                            item.setBackground(FONDO_TARJETA);
                            item.setForeground(Color.WHITE);
                            item.setCursor(new Cursor(Cursor.HAND_CURSOR));
                            item.addActionListener(eventoMenu -> {
                                campoBusqueda.setText(res.getName());
                                menuResultados.setVisible(false);
                                try {
                                    String json = Modelo.traerInfo(res.getName(),"");
                                    ultimaJson = json;
                                    ciudadActual = res.getName();
                                    Modelo.mapInfo(json);
                                } catch (UnsupportedEncodingException ex) {
                                    System.getLogger(WeatherAppUI.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                                }
                                actVentana();
                                lblEstadoGuardado.setText(" ");
                            });
                            menuResultados.add(item);
                        }
                        menuResultados.show(campoBusqueda, 0, campoBusqueda.getHeight());
                    }
                } catch (UnsupportedEncodingException ex) {
                } finally {
                    botonBuscar.setText("Buscar");
                    botonBuscar.setEnabled(true);
                }
            }
        };

        botonBuscar.addActionListener(accionBuscar);
        campoBusqueda.addActionListener(accionBuscar);
        panelBusqueda.add(botonBuscar, BorderLayout.EAST);
        panelBusqueda.add(campoBusqueda, BorderLayout.WEST);
        panelBusqueda.setMaximumSize(new Dimension(310,30));

        JPanel panelDerecho = new JPanel();
        panelDerecho.setLayout(new BoxLayout(panelDerecho, BoxLayout.Y_AXIS));
        panelDerecho.setOpaque(false);

        JPanel panelTituloDerecho = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelTituloDerecho.setOpaque(false);
        panelTituloDerecho.setMaximumSize(new Dimension(Integer.MAX_VALUE,40));
        JLabel titulo = crearLabel("Próximos Días", 20, true, TEXTO_PRINCIPAL);
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        panelTituloDerecho.add(titulo);
        panelDerecho.add(panelTituloDerecho);
        panelDerecho.add(Box.createRigidArea(new Dimension(0, 15)));

        mañana = new PanelPronostico(
                Elementos.Date.getDayOfWeek().plus(1).getDisplayName(TextStyle.FULL, Locale.of("es")).substring(0, 1).toUpperCase()
                .concat(Elementos.Date.getDayOfWeek().plus(1).getDisplayName(TextStyle.FULL, Locale.of("es")).substring(1).toLowerCase()),
                Elementos.daily_will_it_rain[1] == 1 ? "🌧️":"☀️",
                Elementos.maxtemp_c[1]+"° / "+Elementos.mintemp_c[1]+"°"
            );
        panelDerecho.add(mañana);
        pasadoMañana = new PanelPronostico(
                Elementos.Date.getDayOfWeek().plus(2).getDisplayName(TextStyle.FULL, Locale.of("es")).substring(0, 1).toUpperCase()
                .concat(Elementos.Date.getDayOfWeek().plus(2).getDisplayName(TextStyle.FULL, Locale.of("es")).substring(1).toLowerCase()),
                Elementos.daily_will_it_rain[2] == 1 ? "🌧️":"☀️",
                Elementos.maxtemp_c[2]+"° / "+Elementos.mintemp_c[2]+"°"
            );
        panelDerecho.add(Box.createRigidArea(new Dimension(0, 10)));
        panelDerecho.add(pasadoMañana);

        panelDerecho.add(Box.createRigidArea(new Dimension(0, 25)));
        panelDerecho.add(panelBusqueda);

        panelDerecho.add(Box.createRigidArea(new Dimension(0, 15)));

        JLabel lblCiudadesGuardadas = crearLabel("Ciudades guardadas", 14, true, TEXTO_PRINCIPAL);
        panelDerecho.add(lblCiudadesGuardadas);
        panelDerecho.add(Box.createRigidArea(new Dimension(0, 5)));

        comboCiudades = new JComboBox<>();
        comboCiudades.setFont(new Font("Arial", Font.PLAIN, 13));
        comboCiudades.setBackground(new Color(80, 90, 105));
        comboCiudades.setForeground(Color.WHITE);
        comboCiudades.setCursor(new Cursor(Cursor.HAND_CURSOR));
        comboCiudades.setMaximumSize(new Dimension(310, 30));
        comboCiudades.setPreferredSize(new Dimension(310, 30));
        comboCiudades.addItemListener(ev -> {
            if (ev.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
                String seleccion = (String) comboCiudades.getSelectedItem();
                if (seleccion != null && ciudadesGuardadas.containsKey(seleccion)) {
                    ciudadActual = seleccion;
                    ultimaJson = ciudadesGuardadas.get(seleccion);
                    Modelo.mapInfo(ciudadesGuardadas.get(seleccion));
                    actVentana();
                }
            }
        });
        panelDerecho.add(comboCiudades);
        panelDerecho.add(Box.createRigidArea(new Dimension(0, 8)));

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelBotones.setOpaque(false);

        botonGuardar = new JButton("Guardar");
        botonGuardar.setFont(new Font("Arial", Font.PLAIN, 13));
        botonGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botonGuardar.setBackground(new Color(80, 90, 105));
        botonGuardar.setForeground(Color.WHITE);
        botonGuardar.setFocusPainted(false);
        botonGuardar.addActionListener(ev -> {
            if (ciudadActual != null && ultimaJson != null) {
                if (ciudadesGuardadas.containsKey(ciudadActual)) {
                    ciudadesGuardadas.put(ciudadActual, ultimaJson);
                    lblEstadoGuardado.setText("Actualizada");
                } else {
                    ciudadesGuardadas.put(ciudadActual, ultimaJson);
                    comboCiudades.addItem(ciudadActual);
                    comboCiudades.setSelectedItem(ciudadActual);
                    lblEstadoGuardado.setText("Guardada");
                }
            }
        });
        panelBotones.add(botonGuardar);

        JButton botonEliminar = new JButton("Eliminar");
        botonEliminar.setFont(new Font("Arial", Font.PLAIN, 13));
        botonEliminar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botonEliminar.setBackground(new Color(120, 60, 60));
        botonEliminar.setForeground(Color.WHITE);
        botonEliminar.setFocusPainted(false);
        botonEliminar.addActionListener(ev -> {
            String seleccion = (String) comboCiudades.getSelectedItem();
            if (seleccion != null && ciudadesGuardadas.containsKey(seleccion)) {
                ciudadesGuardadas.remove(seleccion);
                comboCiudades.removeItem(seleccion);
                lblEstadoGuardado.setText("Eliminada");
                if (comboCiudades.getItemCount() > 0) {
                    comboCiudades.setSelectedIndex(0);
                }
            }
        });
        panelBotones.add(botonEliminar);

        lblEstadoGuardado = crearLabel(" ", 12, false, TEXTO_SECUNDARIO);
        panelBotones.add(lblEstadoGuardado);

        panelDerecho.add(panelBotones);

        return panelDerecho;
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
        private final JLabel lblTitulo;
        private final JLabel lblValor;
        
        PanelRedondeado(String titulo, String valor) {
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            this.setBackground(FONDO_TARJETA); // Fondo semitransparente
            this.setBorder(new EmptyBorder(15, 15, 15, 15)); // Padding interno
            this.setAlignmentX(Component.LEFT_ALIGNMENT);
            this.setMaximumSize(new Dimension(300, 85)); // Un poco más grandes
            this.setOpaque(false); // Crucial: Java Swing dibuja bordes cuadrados por defecto
            lblTitulo = crearLabel(titulo, 15, false, TEXTO_SECUNDARIO); 
            this.add(lblTitulo);
            this.add(Box.createRigidArea(new Dimension(0, 8))); // Espacio entre título y valor
            lblValor = crearLabel(valor, 20, true, TEXTO_PRINCIPAL);
            this.add(lblValor);
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
        public void actPanelRedondeado(String tituloTexto, String valorTexto){
            lblTitulo.setText(tituloTexto);
            lblValor.setText(valorTexto);
        }
    }

    // --- NUEVO COMPONENTE: FILA DE PRONÓSTICO REDONDEADA ---
    /*private static JPanel crearFilaPronosticoRedondeada(String dia, String icono, String temp) {
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
        diaDespues = crearLabel(dia, 16, false, TEXTO_PRINCIPAL);
        // Reemplazamos crearLabel por crearLabelFila con texto blanco
        fila.add(diaDespues, BorderLayout.WEST);
        
        // Centramos el icono
        iconoDespues = crearLabel(icono, 16, false, TEXTO_PRINCIPAL);
        iconoDespues.setHorizontalAlignment(SwingConstants.CENTER);
        fila.add(iconoDespues, BorderLayout.CENTER);
        
        tempDespues = crearLabel(temp, 16, true, TEXTO_PRINCIPAL);
        fila.add(tempDespues, BorderLayout.EAST);
        
        return fila;
    }*/
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
    private static void actVentana(){

        Color colorFondo = Elementos.is_day.equals("☀️") ? COLOR_FONDO_DIA : COLOR_FONDO_NOCHE;
        panelPrincipal.setBackground(colorFondo);
        barraSuperior.setBackground(colorFondo);
        panelContenido.setBackground(colorFondo);
        lblUbicacion.setText("📍"+Elementos.name+", "+Elementos.country);
        String fecha = Elementos.Date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).localizedBy(Locale.of("es")));
        lblFecha.setText(fecha.substring(0,1).toUpperCase()+fecha.substring(1));
        timer.stop();
        lblIcono.setText(Elementos.is_day+Elementos.cloud);
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LocalTime ahoraReal = LocalTime.now();
                DateTimeFormatter formatoNormal = DateTimeFormatter.ofPattern("hh:mm:ss a");
                DateTimeFormatter formatoMilitar = DateTimeFormatter.ofPattern("HH:mm:ss");
                String textoHora = ahoraReal.format(formatoNormal) + "  |  " + ahoraReal.format(formatoMilitar) + " (24h)";
                lblHora.setText(textoHora);
            }
        });
        timer.start();
        lblTemp.setText(Elementos.temp_c+"°C");
        lblDesc.setText(Elementos.text);
        lblMaxMin.setText("Máx: "+Elementos.maxtemp_c[0]+"° | Min: "+Elementos.mintemp_c[0]+"°");
        panelCentroHumedo.actPanelRedondeado("💧 Humedad", Elementos.humidity+"%");
        panelCentroOreado.actPanelRedondeado("༄ Viento", Elementos.wind_kph+" km/h "+ Elementos.wind_dir);
        panelCentroUv.actPanelRedondeado("☀️ Índice UV", Elementos.getUv()+" ("+ Elementos.uv+")");
        mañana.actValores(Elementos.Date.getDayOfWeek().plus(1).getDisplayName(TextStyle.FULL, Locale.of("es")).substring(0, 1).toUpperCase()
                .concat(Elementos.Date.getDayOfWeek().plus(1).getDisplayName(TextStyle.FULL, Locale.of("es")).substring(1).toLowerCase()), 
                Elementos.daily_will_it_rain[1] == 1 ? "🌧️":"☀️", 
                Elementos.maxtemp_c[1]+"° / "+Elementos.mintemp_c[1]+"°");
        pasadoMañana.actValores(Elementos.Date.getDayOfWeek().plus(2).getDisplayName(TextStyle.FULL, Locale.of("es")).substring(0, 1).toUpperCase()
                .concat(Elementos.Date.getDayOfWeek().plus(2).getDisplayName(TextStyle.FULL, Locale.of("es")).substring(1).toLowerCase()), 
                Elementos.daily_will_it_rain[2] == 1 ? "🌧️":"☀️", 
                Elementos.maxtemp_c[2]+"° / "+Elementos.mintemp_c[2]+"°");
        if (comboCiudades != null && ciudadActual != null && ciudadesGuardadas.containsKey(ciudadActual)) {
            comboCiudades.setSelectedItem(ciudadActual);
        }
        panelContenido.revalidate();
        panelContenido.repaint();
    }

   static class PanelPronostico extends JPanel { // O la clase base redondeada que uses, nya.
            private final JLabel lblDiaDespues;
            private final JLabel lblIconoDespues;
            private final JLabel lblTempDespues;
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(FONDO_TARJETA);
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20); // Radio 20px
            }
            public PanelPronostico(String dia, String icono, String temp) {
                // ... (Aquí copias toda la lógica visual, layouts y colores que tenías dentro de crearFilaPronosticoRedondeada, nya) ...
                setLayout(new BorderLayout());
                this.setOpaque(false); // Transparente para ver el borde redondeado
                this.setBackground(FONDO_TARJETA);
                this.setMaximumSize(new Dimension(350, 45)); // Más ancho y alto
                this.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15)); // Padding interno
                lblDiaDespues = crearLabel(dia, 16, false, TEXTO_PRINCIPAL);
                // Reemplazamos crearLabel por crearLabelFila con texto blanco
                this.add(lblDiaDespues, BorderLayout.WEST);
        
                // Centramos el icono
                lblIconoDespues = crearLabel(icono, 16, false, TEXTO_PRINCIPAL);
                lblIconoDespues.setHorizontalAlignment(SwingConstants.CENTER);
                this.add(lblIconoDespues, BorderLayout.CENTER);

                lblTempDespues = crearLabel(temp, 16, true, TEXTO_PRINCIPAL);
                this.add(lblTempDespues, BorderLayout.EAST);
               
                // ... (Agregas los labels al panel, nya) ...
            }
            public void actValores(String dia, String icono, String temp) {
                this.lblDiaDespues.setText(dia);
                this.lblIconoDespues.setText(icono);
                this.lblTempDespues.setText(temp);
            }
            

            // Este es el método mágico para actualizar solo esta fila, nya.
            
        }
}