package pt.isel.tfm.tc.backend.project.common.model

data class DockerContainer(
        val Id: String,
        val Names: List<String>,
        val Image: String,
        val ImageID: String,
        val Command: String,
        val Created: Long,
        val Ports: List<ContainerPort>,
        val Labels: Map<String, String>,
        val State: String,
        val Status: String,
        val HostConfig: Map<String, Any>,
        val NetworkSettings: Map<String, Any>,
        val Mounts: List<Any>
)

data class ContainerConfig(
        val Image: String,
        val ExposedPorts: Map<String, Map<String, String>>? = null,
        val HostConfig: HostConfiguration? = null
)

data class HostConfiguration(
        val PortBindings: Map<String, List<PortBinding>>? = null
)

data class PortBinding(
        val HostPort: String
)

data class ContainerPort(
        val PrivatePort: Int,
        val Type: String
)

data class DockerBuildResponse(
        val Id: String,
        val Warnings: List<String>
)

data class Container(
        val Id: String,
        val Created: String,
        val Path: String,
        val Args: List<String>,
        val State: State,
        val Image: String,
        val ResolvConfPath: String,
        val HostnamePath: String,
        val HostsPath: String,
        val LogPath: String,
        val Name: String,
        val RestartCount: Int,
        val Driver: String,
        val Platform: String,
        val MountLabel: String,
        val ProcessLabel: String,
        val AppArmorProfile: String,
        val ExecIDs: Any?,
        val HostConfig: HostConfig,
        val GraphDriver: GraphDriver,
        val Mounts: List<Any>,
        val Config: Config,
        val NetworkSettings: NetworkSettings
)

data class State(
        val Status: String,
        val Running: Boolean,
        val Paused: Boolean,
        val Restarting: Boolean,
        val OomKilled: Boolean,
        val Dead: Boolean,
        val Pid: Int,
        val ExitCode: Int,
        val Error: String,
        val StartedAt: String,
        val FinishedAt: String
)

data class HostConfig(
        val Binds: Any?,
        val ContainerIDFile: String,
        val LogConfig: LogConfig,
        val NetworkMode: String,
        val PortBindings: Any?,
        val RestartPolicy: RestartPolicy,
        val AutoRemove: Boolean,
        val VolumeDriver: String,
        val VolumesFrom: Any?,
        val ConsoleSize: List<Int>,
        val CapAdd: Any?,
        val CapDrop: Any?,
        val CgroupnsMode: String,
        val Dns: List<Any>,
        val DnsOptions: List<Any>,
        val DnsSearch: List<Any>,
        val ExtraHosts: Any?,
        val GroupAdd: Any?,
        val IpcMode: String,
        val Cgroup: String,
        val Links: Any?,
        val OomScoreAdj: Int,
        val PidMode: String,
        val Privileged: Boolean,
        val PublishAllPorts: Boolean,
        val ReadonlyRootfs: Boolean,
        val SecurityOpt: Any?,
        val UTSMode: String,
        val UsernsMode: String,
        val ShmSize: Long,
        val Runtime: String,
        val Isolation: String,
        val CpuShares: Int,
        val Memory: Long,
        val NanoCpus: Long,
        val CgroupParent: String,
        val BlkioWeight: Int,
        val BlkioWeightDevice: Any?,
        val BlkioDeviceReadBps: Any?,
        val BlkioDeviceWriteBps: Any?,
        val BlkioDeviceReadIOps: Any?,
        val BlkioDeviceWriteIOps: Any?,
        val CpuPeriod: Int,
        val CpuQuota: Int,
        val CpuRealtimePeriod: Int,
        val CpuRealtimeRuntime: Int,
        val CpusetCpus: String,
        val CpusetMems: String,
        val Devices: Any?,
        val DeviceCgroupRules: Any?,
        val DeviceRequests: Any?,
        val MemoryReservation: Long,
        val MemorySwap: Long,
        val MemorySwappiness: Any?,
        val OomKillDisable: Boolean,
        val PidsLimit: Any?,
        val Ulimits: Any?,
        val CpuCount: Int,
        val CpuPercent: Int,
        val IOMaximumIOps: Long,
        val IOMaximumBandwidth: Long,
        val MaskedPaths: List<String>,
        val ReadonlyPaths: List<String>
)

data class LogConfig(
        val Type: String,
        val Config: Map<String, String>
)

data class RestartPolicy(
        val Name: String,
        val MaximumRetryCount: Int
)

data class GraphDriver(
        val Data: Data,
        val Name: String
)

data class Data(
        val LowerDir: String,
        val MergedDir: String,
        val UpperDir: String,
        val WorkDir: String
)

data class Config(
        val Hostname: String,
        val Domainname: String,
        val User: String,
        val AttachStdin: Boolean,
        val AttachStdout: Boolean,
        val AttachStderr: Boolean,
        val ExposedPorts: Map<String, Map<String, String>>,
        val Tty: Boolean,
        val OpenStdin: Boolean,
        val StdinOnce: Boolean,
        val Env: List<String>,
        val Cmd: List<String>,
        val Image: String,
        val Volumes: Any?,
        val WorkingDir: String,
        val Entrypoint: List<String>,
        val OnBuild: Any?,
        val Labels: Map<String, String>,
        val StopSignal: String
)

data class NetworkSettings(
        val Bridge: String,
        val SandboxID: String,
        val SandboxKey: String,
        val Ports: Map<String, Any>,
        val HairpinMode: Boolean,
        val LinkLocalIPv6Address: String,
        val LinkLocalIPv6PrefixLen: Int,
        val SecondaryIPAddresses: Any?,
        val SecondaryIPv6Addresses: Any?,
        val EndpointID: String,
        val Gateway: String,
        val GlobalIPv6Address: String,
        val GlobalIPv6PrefixLen: Int,
        val IPAddress: String,
        val IPPrefixLen: Int,
        val IPv6Gateway: String,
        val MacAddress: String,
        val Networks: Map<String, Network>
)

data class Network(
        val IPAMConfig: Any?,
        val Links: Any?,
        val Aliases: Any?,
        val MacAddress: String,
        val NetworkID: String,
        val EndpointID: String,
        val Gateway: String,
        val IPAddress: String,
        val IPPrefixLen: Int,
        val IPv6Gateway: String,
        val GlobalIPv6Address: String,
        val GlobalIPv6PrefixLen: Int,
        val DriverOpts: Any?,
        val DNSNames: Any?
)

