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

public class WeatherAppUI {
    static JFrame frame = new JFrame("App de Clima Minimalista");
    
    // --- NUEVA PALETA DE COLORES "ANIME SKY" (Pastel) ---
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
    private static PanelRedondeado panelCentroHumedo;
    private static PanelRedondeado panelCentroOreado;
    private static PanelRedondeado panelCentroUv;
    private static PanelPronostico mañana;
    private static PanelPronostico pasadoMañana;
    
    
    public static void main(String[] args) throws UnsupportedEncodingException {
        // Tu lógica original de datos se mantiene intacta
        // Carga y escritura
        // Si no se puede cargar se escriben los datos anteriores
        // No se le dice un carajo al usuario
        System.setProperty("java.home", ".");
        Modelo.mapInfo(Modelo.traerInfo("Cartagena",""));
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

        // 2. EL TRUCO: Creamos un Panel Maestro
        panelPrincipal = new JPanel();
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
        lblUbicacion = crearLabel("📍"+Elementos.name+", "+Elementos.country, 22, true, TEXTO_PRINCIPAL);
        lblUbicacion.setBorder(new EmptyBorder(0, 0, 0, 0)); // Reseteamos bordes
        String fecha = Elementos.Date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).localizedBy(Locale.of("es")));
        lblFecha = crearLabel(fecha.substring(0,1).toUpperCase()+fecha.substring(1), 15, false, TEXTO_SECUNDARIO);
        
        lblHora = crearLabel("Cargando hora...", 15, true, TEXTO_SECUNDARIO);
        //LocalTime ahora = LocalTime.parse(Elementos.Date.format(DateTimeFormatter.ISO_TIME));
        // Todo ese formateo y parseo se resume a esto:
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

        // ... dentro de tu clase ...
        //Boton de cerrar
        JButton botonCerrar = new JButton("Cerrar");
        botonCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Manita al pasar el ratón
        //botonBuscar.setPreferredSize(new Dimension(75,5));
        // 4. Armamos el panel de búsqueda
        // --- Estilo del Campo de Texto ---
        

        // --- Estilo del Botón ---
        // Te sugiero un color un pelín más claro que el fondo para que resalte
        botonCerrar.setBackground(new Color(80, 90, 105)); // Ajusta este RGB a tu gusto
        botonCerrar.setForeground(Color.WHITE);
        botonCerrar.setFocusPainted(false);
        // 1. Creamos un sub-panel específico para la barra con BorderLayout
        // El '5' es la separación en píxeles entre el campo de texto y el botón
        JPanel panelBusqueda = new JPanel(new BorderLayout(0,0));
        panelBusqueda.setOpaque(false); // Para que respete el color de fondo de tu panel derecho

        // 2. Creamos el campo de texto
        JTextField campoBusqueda = new JTextField(15);
        campoBusqueda.setPreferredSize(new Dimension(300, 25)); // Tamaño sugerido
        campoBusqueda.setFont(new Font("Arial", Font.PLAIN, 14));
        campoBusqueda.setToolTipText("Ej. Cartagena, Bogota, Madrid...");
        campoBusqueda.setBackground(FONDO_TARJETA); // Usa tu color de fondo de tarjeta
        campoBusqueda.setForeground(Color.WHITE);   // Texto blanco
        //campoBusqueda.setCaretColor(Color.WHITE);   // El cursor (la barrita que titila) en blanco
        campoBusqueda.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Quita el borde gris 3D y da margen interno
        // 3. Creamos el botón de buscar
        JButton botonBuscar = new JButton("Buscar");
        botonBuscar.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Manita al pasar el ratón
        //botonBuscar.setPreferredSize(new Dimension(75,5));
        // 4. Armamos el panel de búsqueda
        // --- Estilo del Campo de Texto ---
        

        // --- Estilo del Botón ---
        // Te sugiero un color un pelín más claro que el fondo para que resalte
        botonBuscar.setBackground(new Color(80, 90, 105)); // Ajusta este RGB a tu gusto
        botonBuscar.setForeground(Color.WHITE);
        botonBuscar.setFocusPainted(false); // Quita el cuadrito punteado feo al hacerle clic
        //botonBuscar.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        // 1. Agregar un número de columnas (ej. 15) al crearlo es el truco principal

        // 2. Obligar al componente a no crecer más de la cuenta
        campoBusqueda.setPreferredSize(new Dimension(180,30));
        campoBusqueda.setMinimumSize(new Dimension(180,30));
        campoBusqueda.setMaximumSize(new Dimension(180,30)); // ¡Este es el que evita que empuje al botón!

        // 3. Forzar los colores para evitar el "pantallazo blanco" al escribir
        campoBusqueda.setBackground(new Color(80, 90, 105)); // Usa tu color FONDO_TARJETA aquí
        campoBusqueda.setForeground(Color.WHITE);
        campoBusqueda.setCaretColor(Color.WHITE);

        // Opcional: Un margen interno para que el texto no se pegue a los bordes
        campoBusqueda.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        // ... (Código donde creas el campoBusqueda y botonBuscar) ...

        // Creamos el menú flotante una sola vez
        JPopupMenu menuResultados = new JPopupMenu();
        menuResultados.setBackground(FONDO_TARJETA); // Tu color oscuro
        menuResultados.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 120))); // Borde sutil

        ActionListener accionBuscar = (ActionEvent e) -> {
            
            String textoBuscado = campoBusqueda.getText().trim();
            if (!textoBuscado.isEmpty()) {
                botonBuscar.setText("...");
                botonBuscar.setEnabled(false);
                try {
                    // 1. Llamas a la API de BÚSQUEDA de WeatherAPI
                    menuResultados.removeAll(); // Limpiamos búsquedas anteriores
                    List<DtoBusqueda> resultado = Modelo.buscarInfo(textoBuscado);
                    if (resultado.isEmpty()) {
                        javax.swing.JDialog dialogo = new javax.swing.JDialog();
                        dialogo.setTitle("Busqueda fallida");
                        dialogo.setModal(true); // Para que bloquee el resto de la app hasta que lo cierres
                        dialogo.setSize(350, 150);
                        dialogo.setLocationRelativeTo(null); // Centrar en la pantalla
                        dialogo.setLayout(new java.awt.BorderLayout());

                        // Agregamos el texto centrado
                        javax.swing.JLabel etiquetaTexto = new javax.swing.JLabel("¡No se encontró ninguna ciudad!", javax.swing.SwingConstants.CENTER);
                        dialogo.add(etiquetaTexto, java.awt.BorderLayout.CENTER);

                        // Mostramos la ventana
                        dialogo.setVisible(true);
                        
                    } else if (resultado.size()==1) {
                        // ¡Solo hay una coincidencia! Buscamos el clima de frente
                        String ciudadExacta = resultado.get(0).getNombreCompleto();
                        campoBusqueda.setText(ciudadExacta);
                        // AQUÍ LLAMAS A TU MÉTODO PRINCIPAL QUE TRAE EL CLIMA ACTUAL/PRONÓSTICO
                        Modelo.mapInfo(Modelo.traerInfo(textoBuscado,""));
                        actVentana();
                        
                    } else {
                        // Hay varias opciones (ej. hay muchos "San Juan"). Armamos el menú.
                        for (DtoBusqueda res : resultado) {
                            JMenuItem item = new JMenuItem(res.getNombreCompleto());
                            item.setBackground(FONDO_TARJETA);
                            item.setForeground(Color.WHITE);
                            item.setCursor(new Cursor(Cursor.HAND_CURSOR));
                            
                            // Acción al hacer clic en una opción del menú
                            item.addActionListener(eventoMenu -> {
                                campoBusqueda.setText(res.getName());
                                menuResultados.setVisible(false);
                                
                                try {
                                    // AQUÍ LLAMAS A TU MÉTODO PRINCIPAL QUE TRAE EL CLIMA ACTUAL/PRONÓSTICO
                                    // traerInfoClima(res.getName());
                                    Modelo.mapInfo(Modelo.traerInfo(res.getName(),""));
                                } catch (UnsupportedEncodingException ex) {
                                    System.getLogger(WeatherAppUI.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                                }
                                actVentana();
                            });
                            
                            menuResultados.add(item);
                        }
                        // Mostramos el menú justo debajo de la barra
                        menuResultados.show(campoBusqueda, 0, campoBusqueda.getHeight());
                    }
                    
                } catch (UnsupportedEncodingException ex) {
                } finally {
                    botonBuscar.setText("Buscar");
                    botonBuscar.setEnabled(true);
                }
            }
        };

        //botonBuscar.addActionListener(accionBuscar);
        //campoBusqueda.addActionListener(accionBuscar);
        panelBusqueda.add(botonBuscar, BorderLayout.EAST);
        
        panelBusqueda.add(campoBusqueda, BorderLayout.WEST);
        
        

        // 5. Agregamos el panel de búsqueda a tu panel derecho
        // (Asumiendo que tu panel derecho se llama panelDerecho)
 

        // --- LA MAGIA: CONECTAR LA INTERFAZ CON TU API ---

        // Creamos la acción que se ejecutará al buscar
        /*ActionListener accionBuscar = (ActionEvent e) -> {
            String ciudad = campoBusqueda.getText().trim();
            
            if (!ciudad.isEmpty()) {
                // Cambiamos el texto del botón temporalmente para dar feedback
                botonBuscar.setText("Buscando...");
                botonBuscar.setEnabled(false);
                
                // Aquí llamas a tu método que se conecta a la API y actualiza todo
                // Ejemplo: actualizarClima(ciudad);
                
                // (Si tu método de la API bloquea la pantalla, lo ideal es correrlo en un Thread,
                // pero si es rápido, puedes ponerlo directo. Al terminar, restauras el botón:)
                botonBuscar.setText("Buscar");
                botonBuscar.setEnabled(true);
            } else {
                JOptionPane.showMessageDialog(null, "Por favor, ingresa el nombre de una ciudad.");
            }
        };*/

// 6. Asignamos la misma acción al botón y a la tecla ENTER en el campo de texto
        botonBuscar.addActionListener(accionBuscar);
        campoBusqueda.addActionListener(accionBuscar);
        JPanel panelDerecho = new JPanel();
        panelDerecho.setLayout(new BoxLayout(panelDerecho, BoxLayout.Y_AXIS));
        panelDerecho.setOpaque(false); // Transparente
        
        JPanel panelTituloDerecho = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelTituloDerecho.setOpaque(false); // Transparente para no dañar tu fondo
        panelTituloDerecho.setMaximumSize(new Dimension(Integer.MAX_VALUE,40));
        JLabel titulo = crearLabel("Próximos Días", 20, true, TEXTO_PRINCIPAL);
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 88));
        panelTituloDerecho.add(titulo);
        ActionListener accionCerrar = (ActionEvent e) -> {System.exit(0);};
        botonCerrar.addActionListener(accionCerrar);
        panelTituloDerecho.add(botonCerrar, BorderLayout.AFTER_LINE_ENDS);
        panelDerecho.add(panelTituloDerecho);
        panelBusqueda.setMaximumSize(new Dimension(310,30));
        panelDerecho.add(Box.createRigidArea(new Dimension(0, 15)));
        
        
        // Convertimos cada fila del pronóstico en una pequeña tarjeta redondeada
        mañana = new PanelPronostico(
                Elementos.Date.getDayOfWeek().plus(1).getDisplayName(TextStyle.FULL, Locale.of("es")).substring(0, 1).toUpperCase()
                .concat(Elementos.Date.getDayOfWeek().plus(1).getDisplayName(TextStyle.FULL, Locale.of("es")).substring(1).toLowerCase()), 
                Elementos.daily_will_it_rain[1] == 1 ? "🌧️":"☀️", 
                Elementos.maxtemp_c[1]+"° / "+Elementos.mintemp_c[1]+"°"
            );
       
        panelDerecho.add(mañana);
            // Tu lógica de variables original se mantiene
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

        panelPrincipal.setBackground(Elementos.is_day.equals("☀️") ? COLOR_FONDO_DIA : COLOR_FONDO_NOCHE);
        lblUbicacion.setText("📍"+Elementos.name+", "+Elementos.country);
        String fecha = Elementos.Date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).localizedBy(Locale.of("es")));
        lblFecha.setText(fecha.substring(0,1).toUpperCase()+fecha.substring(1));
        LocalTime ahora = Elementos.Date.toLocalTime();
        timer.stop();
        lblIcono.setText(Elementos.is_day+Elementos.cloud);
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
        frame.repaint();
        frame.revalidate();
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