// Raul Daniel Sanchez Sanchez | 2024-1755

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;

// 1. Clase abstracta para definir atributos comunes y un método abstracto (Abstracción)
abstract class AbstractUsuario {
    private String username;
    private String password;

    public AbstractUsuario(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    public String getUsername() { 
        return username; 
    }
    
    public String getPassword() { 
        return password; 
    }
    
    // Método que se implementará en la subclase (Polimorfismo)
    public abstract void mostrarInfo();
}

// 2. Clase Usuario que hereda de AbstractUsuario (Herencia) y utiliza encapsulamiento
class Usuario extends AbstractUsuario {
    private String nombre;
    private String apellido;
    private String telefono;
    private String email;

    public Usuario(String username, String nombre, String apellido, String telefono, String email, String password) {
        super(username, password);
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.email = email;
    }
    
    public String getNombre() { 
        return nombre; 
    }
    public String getApellido() { 
        return apellido; 
    }
    public String getTelefono() { 
        return telefono; 
    }
    public String getEmail() { 
        return email; 
    }
    
    public void setNombre(String nombre) { 
        this.nombre = nombre; 
    }
    public void setApellido(String apellido) { 
        this.apellido = apellido; 
    }
    public void setTelefono(String telefono) { 
        this.telefono = telefono; 
    }
    public void setEmail(String email) { 
        this.email = email; 
    }
    
    @Override
    public void mostrarInfo() {
        System.out.println("Usuario: " + getUsername() + ", Nombre: " + nombre + " " + apellido +
                           ", Teléfono: " + telefono + ", Email: " + email);
    }
}

// 3. Interfaz que define operaciones para actualizar y eliminar usuarios
interface UserOperations {
    void actualizarUsuario(Usuario user);
    void eliminarUsuario(String username);
}

// 4. Clase UserManager que administra la lista de usuarios y usa el patrón Singleton
class UserManager implements UserOperations {
    private ArrayList<Usuario> usuarios;
    
    // Variable para almacenar la única instancia de UserManager
    private static UserManager instancia = null;
    
    // Constructor privado para evitar instanciación desde fuera
    private UserManager() {
        usuarios = new ArrayList<>();
    }
    
    // Método estático para obtener la instancia única (Singleton)
    public static UserManager getInstance() {
        if (instancia == null) {
            instancia = new UserManager();
        }
        return instancia;
    }
    
    // Método para registrar un nuevo usuario (verifica que no exista previamente)
    public boolean registrarUsuario(Usuario usuario) {
        for (Usuario u : usuarios) {
            if (u.getUsername().equals(usuario.getUsername())) {
                return false; // El usuario ya existe
            }
        }
        usuarios.add(usuario);
        return true;
    }
    
    // Método para validar el login
    public Usuario validarLogin(String username, String password) {
        for (Usuario u : usuarios) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                return u;
            }
        }
        return null;
    }
    
    public ArrayList<Usuario> obtenerUsuarios() {
        return usuarios;
    }
    
    // Actualizar los datos de un usuario
    @Override
    public void actualizarUsuario(Usuario user) {
        for (int i = 0; i < usuarios.size(); i++) {
            if (usuarios.get(i).getUsername().equals(user.getUsername())) {
                usuarios.set(i, user);
                break;
            }
        }
    }
    
    // Eliminar un usuario por su nombre de usuario
    @Override
    public void eliminarUsuario(String username) {
        usuarios.removeIf(u -> u.getUsername().equals(username));
    }
}

// 5. Clase principal que inicia la aplicación mostrando el Login
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame());
    }
}

// 6. Ventana de Login
class LoginFrame extends JFrame {
    private JTextField tfUsername;
    private JPasswordField pfPassword;
    private JButton btnLogin, btnRegister;
    private UserManager userManager;

    public LoginFrame() {
        // Se obtiene la instancia única de UserManager
        userManager = UserManager.getInstance();
        setTitle("Inicio de Sesión");
        setSize(400, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
        setVisible(true);
    }
    
    // Inicialización de componentes gráficos
    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.add(new JLabel("Usuario:"));
        tfUsername = new JTextField();
        panel.add(tfUsername);
        
        panel.add(new JLabel("Contraseña:"));
        pfPassword = new JPasswordField();
        panel.add(pfPassword);
        
        btnLogin = new JButton("Iniciar Sesión");
        btnLogin.addActionListener(e -> login());
        panel.add(btnLogin);
        
        btnRegister = new JButton("Registrarse");
        // Abre la ventana de registro al presionar el botón
        btnRegister.addActionListener(e -> new RegistrationFrame(userManager, this));
        panel.add(btnRegister);
        
        add(panel, BorderLayout.CENTER);
    }
    
    // Lógica para iniciar sesión
    private void login() {
        String username = tfUsername.getText().trim();
        String password = new String(pfPassword.getPassword()).trim();
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar su usuario y contraseña, si no está registrado debe registrarse");
            return;
        }
        
        Usuario user = userManager.validarLogin(username, password);
        if (user != null) {
            // Si el login es correcto, se abre la ventana principal y se oculta el login
            new MainFrame(userManager, this);
            setVisible(false);
            clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos");
        }
    }
    
    // Limpia los campos de texto
    public void clearFields() {
        tfUsername.setText("");
        pfPassword.setText("");
    }
    
    // Muestra la ventana de login
    public void mostrar() {
        setVisible(true);
    }
}

// 7. Ventana de Registro
class RegistrationFrame extends JFrame {
    private JTextField tfUsername, tfNombre, tfApellido, tfTelefono, tfEmail;
    private JPasswordField pfPassword, pfConfirmPassword;
    private JButton btnSubmit;
    private UserManager userManager;
    private JFrame loginFrame;
    
    public RegistrationFrame(UserManager userManager, JFrame loginFrame) {
        this.userManager = userManager;
        this.loginFrame = loginFrame;
        setTitle("Registro");
        setSize(300, 200);
        setLocationRelativeTo(null);
        initComponents();
        setVisible(true);
    }
    
    // Inicialización de componentes del registro
    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(7, 2, 5, 5));
        panel.add(new JLabel("Nombre de Usuario:"));
        tfUsername = new JTextField();
        panel.add(tfUsername);
        
        panel.add(new JLabel("Nombre:"));
        tfNombre = new JTextField();
        panel.add(tfNombre);
        
        panel.add(new JLabel("Apellido:"));
        tfApellido = new JTextField();
        panel.add(tfApellido);
        
        panel.add(new JLabel("Teléfono:"));
        tfTelefono = new JTextField();
        panel.add(tfTelefono);
        
        panel.add(new JLabel("Correo:"));
        tfEmail = new JTextField();
        panel.add(tfEmail);
        
        panel.add(new JLabel("Contraseña:"));
        pfPassword = new JPasswordField();
        panel.add(pfPassword);
        
        panel.add(new JLabel("Confirmar Contraseña:"));
        pfConfirmPassword = new JPasswordField();
        panel.add(pfConfirmPassword);
        
        btnSubmit = new JButton("Registrar");
        btnSubmit.addActionListener(e -> registrar());
        
        add(panel, BorderLayout.CENTER);
        add(btnSubmit, BorderLayout.SOUTH);
    }
    
    // Lógica de registro
    private void registrar() {
        String username = tfUsername.getText().trim();
        String nombre = tfNombre.getText().trim();
        String apellido = tfApellido.getText().trim();
        String telefono = tfTelefono.getText().trim();
        String email = tfEmail.getText().trim();
        String password = new String(pfPassword.getPassword()).trim();
        String confirmPassword = new String(pfConfirmPassword.getPassword()).trim();
        
        // Validaciones de cada campo
        if(username.isEmpty()){
            JOptionPane.showMessageDialog(this, "Falta el nombre de usuario");
            return;
        }
        if(nombre.isEmpty()){
            JOptionPane.showMessageDialog(this, "Falta el nombre");
            return;
        }
        if(apellido.isEmpty()){
            JOptionPane.showMessageDialog(this, "Falta el apellido");
            return;
        }
        if(telefono.isEmpty()){
            JOptionPane.showMessageDialog(this, "Falta el teléfono");
            return;
        }
        if(email.isEmpty()){
            JOptionPane.showMessageDialog(this, "Falta el correo");
            return;
        }
        if(password.isEmpty()){
            JOptionPane.showMessageDialog(this, "Falta la contraseña");
            return;
        }
        if(confirmPassword.isEmpty()){
            JOptionPane.showMessageDialog(this, "Falta confirmar la contraseña");
            return;
        }
        if(!password.equals(confirmPassword)){
            JOptionPane.showMessageDialog(this, "Las contraseñas no coinciden");
            return;
        }
        
        Usuario nuevoUsuario = new Usuario(username, nombre, apellido, telefono, email, password);
        if(userManager.registrarUsuario(nuevoUsuario)){
            JOptionPane.showMessageDialog(this, "Registro exitoso");
            dispose(); // Cierra la ventana de registro
        } else {
            JOptionPane.showMessageDialog(this, "El usuario ya existe");
        }
    }
}

// 8. Ventana principal que muestra el listado de usuarios y permite actualizar/eliminar
class MainFrame extends JFrame {
    private UserManager userManager;
    private JFrame loginFrame;
    private JTable table;
    private UserTableModel tableModel;
    private JButton btnCerrarSesion, btnActualizar, btnEliminar;
    
    public MainFrame(UserManager userManager, JFrame loginFrame) {
        this.userManager = userManager;
        this.loginFrame = loginFrame;
        setTitle("Usuarios Registrados");
        setSize(600, 400);
        setLocationRelativeTo(null);
        initComponents();
        setVisible(true);
    }
    
    // Inicialización de la tabla y botones
    private void initComponents() {
        tableModel = new UserTableModel(userManager.obtenerUsuarios());
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        
        btnCerrarSesion = new JButton("Cerrar Sesión");
        btnCerrarSesion.addActionListener(e -> cerrarSesion());
        
        btnActualizar = new JButton("Actualizar Usuario");
        btnActualizar.addActionListener(e -> actualizarUsuario());
        
        btnEliminar = new JButton("Eliminar Usuario");
        btnEliminar.addActionListener(e -> eliminarUsuario());
        
        JPanel panelBotones = new JPanel();
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnCerrarSesion);
        
        add(scrollPane, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }
    
    // Cierra la sesión y vuelve al login
    private void cerrarSesion() {
        dispose();
        loginFrame.setVisible(true);
    }
    
    // Llama a la ventana para actualizar el usuario seleccionado
    private void actualizarUsuario() {
        int selectedRow = table.getSelectedRow();
        if(selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario para actualizar");
            return;
        }
        String username = (String) tableModel.getValueAt(selectedRow, 0);
        Usuario usuario = null;
        for(Usuario u : userManager.obtenerUsuarios()){
            if(u.getUsername().equals(username)){
                usuario = u;
                break;
            }
        }
        if(usuario != null){
            new UpdateUserFrame(userManager, usuario, tableModel);
        }
    }
    
    // Elimina el usuario seleccionado
    private void eliminarUsuario() {
        int selectedRow = table.getSelectedRow();
        if(selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario para eliminar");
            return;
        }
        String username = (String) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar usuario " + username + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if(confirm == JOptionPane.YES_OPTION){
            userManager.eliminarUsuario(username);
            tableModel.setUsuarios(userManager.obtenerUsuarios());
            tableModel.fireTableDataChanged();
        }
    }
}

// 9. Ventana para actualizar los datos de un usuario
class UpdateUserFrame extends JFrame {
    private JTextField tfNombre, tfApellido, tfTelefono, tfEmail;
    private JButton btnActualizar;
    private UserManager userManager;
    private Usuario usuario;
    private UserTableModel tableModel;
    
    public UpdateUserFrame(UserManager userManager, Usuario usuario, UserTableModel tableModel) {
        this.userManager = userManager;
        this.usuario = usuario;
        this.tableModel = tableModel;
        setTitle("Actualizar Usuario: " + usuario.getUsername());
        setSize(400, 250);
        setLocationRelativeTo(null);
        initComponents();
        setVisible(true);
    }
    
    // Inicializa los campos con la información actual del usuario
    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.add(new JLabel("Nombre:"));
        tfNombre = new JTextField(usuario.getNombre());
        panel.add(tfNombre);
        
        panel.add(new JLabel("Apellido:"));
        tfApellido = new JTextField(usuario.getApellido());
        panel.add(tfApellido);
        
        panel.add(new JLabel("Teléfono:"));
        tfTelefono = new JTextField(usuario.getTelefono());
        panel.add(tfTelefono);
        
        panel.add(new JLabel("Correo:"));
        tfEmail = new JTextField(usuario.getEmail());
        panel.add(tfEmail);
        
        btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> actualizar());
        
        add(panel, BorderLayout.CENTER);
        add(btnActualizar, BorderLayout.SOUTH);
    }
    
    // Lógica para actualizar los datos del usuario
    private void actualizar() {
        String nombre = tfNombre.getText().trim();
        String apellido = tfApellido.getText().trim();
        String telefono = tfTelefono.getText().trim();
        String email = tfEmail.getText().trim();
        
        if(nombre.isEmpty() || apellido.isEmpty() || telefono.isEmpty() || email.isEmpty()){
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios");
            return;
        }
        
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setTelefono(telefono);
        usuario.setEmail(email);
        userManager.actualizarUsuario(usuario);
        tableModel.fireTableDataChanged();
        JOptionPane.showMessageDialog(this, "Usuario actualizado correctamente");
        dispose();
    }
}

// 10. Modelo de tabla para mostrar la lista de usuarios
class UserTableModel extends AbstractTableModel {
    private List<Usuario> usuarios;
    private String[] columnNames = {"Usuario", "Nombre Completo", "Teléfono", "Correo"};
    
    public UserTableModel(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }
    
    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }
    
    @Override
    public int getRowCount() {
        return usuarios.size();
    }
    
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Usuario u = usuarios.get(rowIndex);
        switch(columnIndex) {
            case 0: return u.getUsername();
            case 1: return u.getNombre() + " " + u.getApellido();
            case 2: return u.getTelefono();
            case 3: return u.getEmail();
            default: return null;
        }
    }
    
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
}
