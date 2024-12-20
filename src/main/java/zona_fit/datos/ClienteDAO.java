package zona_fit.datos;

import zona_fit.dominio.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static zona_fit.conexion.Conexion.getConexion;

public class ClienteDAO implements IClienteDAO {
    private Logger logger;

    public ClienteDAO() {
        this.logger = Logger.getLogger("ZonaFitLogger");
    }

    @Override
    public List<Cliente> listarClientes() {
        List<Cliente> clientes = new ArrayList<>();
        PreparedStatement ps;
        ResultSet rs;
        Connection con = getConexion();
        String sql = "SELECT * FROM cliente ORDER BY id";
        try {
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Cliente cliente = new Cliente();
                cliente.setId(rs.getInt("id"));
                cliente.setNombre(rs.getString("nombre"));
                cliente.setApellido(rs.getString("apellido"));
                cliente.setMembresia(rs.getInt("membresia"));
                clientes.add(cliente);
            }
        } catch (Exception e) {
            this.logger.log(Level.SEVERE, "Error al listar clientes: " + e.getMessage());
        } finally {
            try {
                con.close();
            } catch (Exception e) {
                this.logger.log(Level.SEVERE, String.format("Error al cerrar conexión: %s", e.getMessage()));
            }
        }
        return clientes;
    }

    @Override
    public boolean buscarClientePorId(Cliente cliente) {
        PreparedStatement ps;
        ResultSet rs;
        Connection con = getConexion();
        String sql = "SELECT * FROM cliente WHERE id = ?";
        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, cliente.getId());
            rs = ps.executeQuery();
            if (rs.next()) {
                cliente.setNombre(rs.getString("nombre"));
                cliente.setApellido(rs.getString("apellido"));
                cliente.setMembresia(rs.getInt("membresia"));
                return true;
            }
        } catch (Exception e) {
            this.logger.log(Level.SEVERE, String.format("Error al obtener el cliente por id: %s", e.getMessage()));
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                this.logger.log(Level.SEVERE, String.format("Error al cerrar conexión: %s", e.getMessage()));
            }
        }
        return false;
    }

    @Override
    public boolean agregarCliente(Cliente cliente) {
        PreparedStatement ps;
        Connection con = getConexion();
        String sql = "INSERT INTO cliente(nombre, apellido, membresia) VALUES(?, ?, ?)";
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getApellido());
            ps.setInt(3, cliente.getMembresia());
            ps.execute();
            return true;
        } catch (SQLException sqlE) {
            this.logger.log(Level.SEVERE, sqlE.getMessage());
        } catch (Exception e) {
            this.logger.log(Level.SEVERE, "error al agregar cliente: " + e.getMessage());
        }
        finally {
            try {

                con.close();
            } catch (Exception e) {
                this.logger.log(Level.SEVERE, "Error al cerrar la conexión: " + e.getMessage());
            }
        }
        return false;
    }

    @Override
    public boolean modificarCliente(Cliente cliente) {
        return false;
    }

    @Override
    public boolean eliminarCliente(Cliente cliente) {
        return false;
    }

    public static void main(String[] args) {
        ClienteDAO clienteDao = new ClienteDAO();


//        Cliente cliente = new Cliente(1);
//        boolean encontrado = clienteDao.buscarClientePorId(cliente);
//        if (encontrado) {
//            clienteDao.logger.log(Level.INFO, "Cliente encontrado: {0}", cliente);
//        }

        Cliente nuevoCliente = new Cliente("Jill", "Valentine", 413);
        boolean agregado = clienteDao.agregarCliente(nuevoCliente);
        if (agregado) {
            clienteDao.logger.log(Level.INFO, "Se agregó el cliente: " + nuevoCliente);
        } else {
            clienteDao.logger.log(Level.SEVERE, "No se agregó el cliente: " + nuevoCliente);
        }

        clienteDao.logger.log(Level.INFO,"Clientes");
        List<Cliente> clientes = clienteDao.listarClientes();
        clientes.forEach(System.out::println);
    }
}
