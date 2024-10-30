package com.university.config;

import java.util.ArrayList;
import java.util.Optional;
import java.time.LocalTime;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.university.enums.DiaSemanaEnum;
import com.university.enums.EstadoReservacionEnum;
import com.university.enums.MetodoPagoEnum;
import com.university.enums.PermisoEnum;
import com.university.models.ConfiguracionGlobal;
import com.university.models.DiaAtencion;
import com.university.models.DuracionServicio;
import com.university.models.EstadoReservacion;
import com.university.models.HorarioAtencionServicio;
import com.university.models.MetodoPago;
import com.university.models.Negocio;
import com.university.models.Permiso;
import com.university.models.Recurso;
import com.university.models.Rol;
import com.university.models.Servicio;
import com.university.models.TipoServicio;
import com.university.models.UnidadRecurso;
import com.university.models.Usuario;
import com.university.models.request.CreateServicioDto;
import com.university.repositories.ConfiguracionGlobalRepository;
import com.university.repositories.DiaAtencionRepository;
import com.university.repositories.EstadoReservacionRepository;
import com.university.repositories.MetodoPagoRepository;
import com.university.repositories.NegocioRepository;
import com.university.repositories.PermisoRepository;
import com.university.repositories.RecursoRepository;
import com.university.repositories.RolRepository;
import com.university.repositories.ServicioRepository;
import com.university.repositories.TipoServicioRepository;
import com.university.repositories.UnidadRecursoRepository;
import com.university.services.NegocioService;
import com.university.services.RolService;
import com.university.services.ServicioService;
import com.university.services.TipoServicioService;
import com.university.services.UsuarioService;

@Component
public class Inserts implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private RolRepository rolRepository;
    @Autowired
    private PermisoRepository permisoRepository;
    @Autowired
    private DiaAtencionRepository diaAtencionRepository;
    @Autowired
    private TipoServicioRepository tipoServicioRepository;
    @Autowired
    private ConfiguracionGlobalRepository configuracionGlobalRepository;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private RolService rolService;
    @Autowired
    private TipoServicioService tipoServicioService;
    @Autowired
    private NegocioRepository negocioRepository;
    @Autowired
    private NegocioService negocioService;
    @Autowired
    private ServicioService servicioService;
    @Autowired
    private ServicioRepository servicioRepository;
    @Autowired
    private RecursoRepository recursoRepository;
    @Autowired
    private UnidadRecursoRepository unidadRecursoRepository;
    @Autowired
    private EstadoReservacionRepository estadoReservacionRepository;
    @Autowired
    private MetodoPagoRepository metodoPagoRepository;

    public Negocio insertarNegocio(Negocio negocio) throws Exception {
        try {
            Optional<Negocio> opNegocio = this.negocioRepository.findOneByNombre(negocio.getNombre());
            if (opNegocio.isPresent()) {
                return opNegocio.get();
            }
            return this.negocioRepository.save(negocio);
        } catch (Exception e) {
            System.out.println(e);
            throw new Exception("Error");
        }
    }

    public Servicio insertarServicio(Servicio servicio) throws Exception {
        try {
            Optional<Servicio> opServicio = this.servicioRepository.findOneByNombre(servicio.getNombre());
            if (opServicio.isPresent()) {
                return opServicio.get();
            }
            return this.servicioRepository.save(servicio);
        } catch (Exception e) {
            System.out.println(e);
            throw new Exception("Error");
        }
    }

    public Recurso insertarRecurso(Recurso recurso) throws Exception {
        try {
            Optional<Recurso> opRecurso = this.recursoRepository.findOneByNombre(recurso.getNombre());
            if (opRecurso.isPresent()) {
                return opRecurso.get();
            }
            return this.recursoRepository.save(recurso);
        } catch (Exception e) {
            System.out.println(e);
            throw new Exception("Error");
        }
    }

    public UnidadRecurso insertarUnidadRecurso(UnidadRecurso unidadRecurso, Recurso recurso) throws Exception {
        try {
            Optional<UnidadRecurso> opUnidadRecurso = this.unidadRecursoRepository.findByNombreAndRecurso_Nombre(unidadRecurso.getNombre(), recurso.getNombre());
            if (opUnidadRecurso.isPresent()) {
                return opUnidadRecurso.get();
            }
            return this.unidadRecursoRepository.save(unidadRecurso);
        } catch (Exception e) {
            System.out.println(e);
            throw new Exception("Error");
        }
    }

    public Rol insertarRol(Rol rol) throws Exception {
        try {
            Optional<Rol> opRol = this.rolRepository.findOneByNombre(rol.getNombre());
            if (opRol.isPresent()) {
                return opRol.get();
            }
            return this.rolRepository.save(rol);
        } catch (Exception e) {
            System.out.println(e);
            throw new Exception("Error");
        }
    }

    public TipoServicio insertarTipoServicio(TipoServicio tipoServicio) throws Exception {
        try {
            Optional<TipoServicio> opTipoServicio = this.tipoServicioRepository
                    .findOneByNombre(tipoServicio.getNombre());
            if (opTipoServicio.isPresent()) {
                return opTipoServicio.get();
            }
            return this.tipoServicioRepository.save(tipoServicio);
        } catch (Exception e) {
            System.out.println(e);
            throw new Exception("Error");
        }
    }

    public Permiso insertarPermiso(Permiso permiso) throws Exception {
        try {
            Optional<Permiso> opPermiso = this.permisoRepository.findOneByNombre(permiso.getNombre());
            if (opPermiso.isPresent()) {
                return opPermiso.get();
            }
            return this.permisoRepository.save(permiso);
        } catch (Exception e) {
            System.out.println(e);
            throw new Exception("Error");
        }
    }

    public DiaAtencion insertarDiaAtencion(DiaAtencion diaAtencion) throws Exception {
        try {
            Optional<DiaAtencion> opDiaAtencion = this.diaAtencionRepository
                    .findOneByNombre(diaAtencion.getNombre().toString());
            if (opDiaAtencion.isPresent()) {
                return opDiaAtencion.get();
            }
            return this.diaAtencionRepository.save(diaAtencion);
        } catch (Exception e) {
            System.out.println(e);
            throw new Exception("Error");
        }
    }

    public EstadoReservacion insertarEstadoReservacion(EstadoReservacion estadoReservacion) throws Exception {
        try {
            Optional<EstadoReservacion> opEstadoReservacion = this.estadoReservacionRepository
                    .findOneByNombre(estadoReservacion.getNombre().toString());
            if (opEstadoReservacion.isPresent()) {
                return opEstadoReservacion.get();
            }
            return this.estadoReservacionRepository.save(estadoReservacion);
        } catch (Exception e) {
            System.out.println(e);
            throw new Exception("Error");
        }
    }

    public MetodoPago insertarMetodoPago(MetodoPago metodoPago) throws Exception {
        try {
            Optional<MetodoPago> opMetodoPago = this.metodoPagoRepository
                    .findOneByNombre(metodoPago.getNombre().toString());
            if (opMetodoPago.isPresent()) {
                return opMetodoPago.get();
            }
            return this.metodoPagoRepository.save(metodoPago);
        } catch (Exception e) {
            System.out.println(e);
            throw new Exception("Error");
        }
    }

    public ConfiguracionGlobal insertarConfiguracionGlobal(ConfiguracionGlobal config) throws Exception {
        try {
            ConfiguracionGlobal conf = this.configuracionGlobalRepository.findFirstByOrderByIdAsc().orElse(null);
            if (conf == null) {
                return this.configuracionGlobalRepository.save(config);
            }
            return conf;

        } catch (Exception e) {
            throw new Exception("Error");
        }
    }

    public Permiso insertarPermisoSiNoExiste(Permiso pa) {
        Permiso permiso = this.permisoRepository.findOneByNombre(pa.getNombre()).orElse(null);
        if (permiso == null) {
            return this.permisoRepository.save(
                    pa);
        }
        return permiso;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            //Dias Atencion
            for (DiaSemanaEnum diaSemana : DiaSemanaEnum.values()) {
                // Se crea el dia si no existe
                this.insertarDiaAtencion(
                        new DiaAtencion(
                                diaSemana.getNombre()));
            }

            //Estados Reservacion
            for (EstadoReservacionEnum estadoReservacion : EstadoReservacionEnum.values()) {
                // Se crea el dia si no existe
                this.insertarEstadoReservacion(
                        new EstadoReservacion(
                                estadoReservacion.getNombre()));
            }

            //Metodos Pago
            for (MetodoPagoEnum metodoPago : MetodoPagoEnum.values()) {
                // Se crea el dia si no existe
                this.insertarMetodoPago(
                        new MetodoPago(
                                metodoPago.getNombre()));
            }

            // Negocios
            Negocio negocioBarberia = this.insertarNegocio(new Negocio("Barberia Santos"));
            Negocio negocioCancha = this.insertarNegocio(new Negocio("Renta de Canchas"));

            // Roles
            this.insertarRol(new Rol("USUARIO"));
            this.insertarRol(new Rol("ADMIN"));

            // Recursos
            Recurso recursoCancha = this.insertarRecurso(new Recurso("Cancha"));

            UnidadRecurso unidadRecursoCancha1 = this.insertarUnidadRecurso(new UnidadRecurso("Cancha 1", recursoCancha), recursoCancha);
            UnidadRecurso unidadRecursoCancha2 = this.insertarUnidadRecurso(new UnidadRecurso("Cancha 2", recursoCancha), recursoCancha);

            // Tipos de Servicio
            TipoServicio tipoSimple = this.insertarTipoServicio(new TipoServicio("Simple"));
            TipoServicio tipoRecurso = this.insertarTipoServicio(new TipoServicio("Recurso"));

            // Servicios
            DiaAtencion diaAtencionLunes = this.insertarDiaAtencion(new DiaAtencion("Lunes"));
            DiaAtencion diaAtencionMartes = this.insertarDiaAtencion(new DiaAtencion("Martes"));

            ArrayList<HorarioAtencionServicio> horarioAtencionServiciosBarberia = new ArrayList<>();
            horarioAtencionServiciosBarberia.add(
                    new HorarioAtencionServicio(LocalTime.of(8, 0), LocalTime.of(12, 0), diaAtencionLunes));
            horarioAtencionServiciosBarberia.add(
                    new HorarioAtencionServicio(LocalTime.of(8, 0), LocalTime.of(12, 0), diaAtencionMartes));
            Optional<Servicio> servicioBarberia = this.servicioRepository.findOneByNombre("Corte de Pelo");

            try {
                if (servicioBarberia.isEmpty()) {
                    this.servicioService.createServicio(new CreateServicioDto(
                            new Servicio(
                                    "Corte de Pelo",
                                    tipoSimple,
                                    null,
                                    negocioBarberia,
                                    50,
                                    1,
                                    90,
                                    2,
                                    false),
                            new DuracionServicio(30, 0),
                            horarioAtencionServiciosBarberia

                    ));
                }
            } catch (Exception e) {
                System.out.println(e);
                throw new Exception("Error");
            }
            ArrayList<HorarioAtencionServicio> horarioAtencionServiciosCancha = new ArrayList<>();
            horarioAtencionServiciosCancha.add(
                    new HorarioAtencionServicio(LocalTime.of(8, 0), LocalTime.of(12, 0), diaAtencionLunes));
            horarioAtencionServiciosCancha.add(
                    new HorarioAtencionServicio(LocalTime.of(8, 0), LocalTime.of(12, 0), diaAtencionMartes));

            Optional<Servicio> servicioCancha = this.servicioRepository.findOneByNombre("Renta de Canchas");

            try {
                if (servicioCancha.isEmpty()) {
                    this.servicioService.createServicio(new CreateServicioDto(
                            new Servicio(
                                    "Renta de Canchas",
                                    tipoRecurso,
                                    recursoCancha,
                                    negocioCancha,
                                    100,
                                    4,
                                    50,
                                    1,
                                    true),
                            new DuracionServicio(0, 2),
                            horarioAtencionServiciosCancha));
                }
            } catch (Exception e) {
                System.out.println(e);
                throw new Exception("Error");
            }

            // IMAGEN DEFAULT DE LA TIENDA
            byte[] img = getClass().getResourceAsStream("/img/logo.png").readAllBytes();

            ConfiguracionGlobal configuracionGlobal = new ConfiguracionGlobal("Reservaciones GT", img);
            this.insertarConfiguracionGlobal(configuracionGlobal);

            // Seeder de usuarios del sistema
            Usuario admin = new Usuario(
                    "admin",
                    "admin",
                    "email@gmail.com",
                    null,
                    null,
                    "12345",
                    null,
                    null,
                    false,
                    null);
            Usuario usuario = new Usuario(
                    "Carlos",
                    "Pac",
                    "carlosbpac@gmail.com",
                    null,
                    null,
                    "12345",
                    null,
                    null,
                    false,
                    null);

            /*
             * Crear los usuarios, un catch por usuario para que ignore las
             * excepciones que puedan haber
             */
            try {
                this.usuarioService.crearAdministrador(admin);
            } catch (Exception e) {
            }

            try {
                this.usuarioService.crearUsuarioNormal(usuario);
            } catch (Exception e) {
            }

            // Creacion de todos los permisos que tiene el sistema
            for (PermisoEnum permiso : PermisoEnum.values()) {
                // Se crea el permiso si no existe
                Permiso permisoInsercion = this.insertarPermiso(
                        new Permiso(
                                permiso.getNombrePermiso(),
                                permiso.getRuta()));

                // Si el permiso es ADMIN se asigna al mismo rol
                if (permisoInsercion.getNombre().equals("ADMINISTRADOR")) {
                    Rol rolAdmin = this.rolService.getRol("ADMIN");
                    try {
                        this.rolService.agregarPermisoRol(rolAdmin, permisoInsercion);
                    } catch (IllegalArgumentException e) {
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
            }


        } catch (Exception ex) {
            Logger.getLogger(Inserts.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
