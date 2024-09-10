export type Prompt = {
    text: string,
    date: string | undefined,
    author:boolean
}

export enum ContainerAction {
    Start = 1,
    Stop = 2
}

export type CurrentPromptInfo ={
    prompts:Array<Prompt>,
    id:string | ""
}

export type Chat = {
    id: string,
    description: string,
    owner: string,
    date: string
}   

export type DockerContainer = {
    id: string,
    names: Array<string>,
    image: string,
    imageID: string,
    command: string,
    created: number,
    ports: Array<string>,
    labels: Map<string, string>,
    state: string,
    status: string,
    hostConfig: Map<string, unknown>,
    networkSettings: Map<string, unknown>,
    mounts: Array<unknown>
}

// names, imageID, command, ports
export type Container = {
    name: string;
    id: string;
    state: DockerState;
    path: string;
    args: string[];
    config: DockerConfig;
    platform: string;
    created: string;
    resolvConfPath: string;
    hostnamePath: string;
    image: string;
    networkSettings: DockerNetworkSettings;
    logPath: string;
    driver: string;
    appArmorProfile: string;
    processLabel: string;
    restartCount: number;
    mountLabel: string;
    mounts: any[]; // Assuming an array for mounts
    hostConfig: DockerHostConfig;
    execIDs: null;
    graphDriver: DockerGraphDriver;
    hostsPath: string;
}

export type DockerState = {
    exitCode: number;
    status: string;
    pid: number;
    paused: boolean;
    error: string;
    restarting: boolean;
    startedAt: string;
    running: boolean;
    oomKilled: boolean;
    dead: boolean;
    finishedAt: string;
};

type DockerConfig = {
    labels: {
        maintainer: string;
    };
    env: string[];
    hostname: string;
    user: string;
    domainname: string;
    attachStdin: boolean;
    workingDir: string;
    cmd: string[];
    exposedPorts: {
        [port: string]: {}; // Assuming empty object for each port
    };
    attachStdout: boolean;
    onBuild: null;
    stopSignal: string;
    attachStderr: boolean;
    tty: boolean;
    volumes: null;
    openStdin: boolean;
    entrypoint: string[];
    stdinOnce: boolean;
    image: string;
};

type DockerNetwork = {
    aliases: null;
    ipamconfig: null;
    driverOpts: null;
    networkID: string;
    dnsnames: null;
    ipaddress: string;
    gateway: string;
    macAddress: string;
    ipv6Gateway: string;
    ipprefixLen: number;
    endpointID: string;
    globalIPv6Address: string;
    globalIPv6PrefixLen: number;
    links: null;
};

type DockerNetworkSettings = {
    bridge: string;
    ipaddress: string;
    networks: {
        bridge: DockerNetwork;
    };
    gateway: string;
    sandboxKey: string;
    macAddress: string;
    ipv6Gateway: string;
    ipprefixLen: number;
    sandboxID: string;
    endpointID: string;
    hairpinMode: boolean;
    ports: {};  // Assuming an empty object for ports
    secondaryIPv6Addresses: null;
    globalIPv6Address: string;
    globalIPv6PrefixLen: number;
    linkLocalIPv6Address: string;
    secondaryIPAddresses: null;
    linkLocalIPv6PrefixLen: number;
};

type DockerLogConfig = {
    type: string;
    config: {}; // Assuming empty object for config
};

type DockerRestartPolicy = {
    name: string;
    maximumRetryCount: number;
};

type PortBinding = {
    HostIp: string | undefined;
    HostPort: string | undefined;
}

type DockerHostConfig = {
    runtime: string;
    cpuPeriod: number;
    cpuQuota: number;
    cpuShares: number;
    privileged: boolean;
    memoryReservation: number;
    memorySwappiness: number | null;
    blkioWeightDevice: null;
    blkioDeviceReadBps: null;
    blkioDeviceReadIOps: null;
    blkioDeviceWriteIOps: null;
    blkioDeviceWriteBps: null;
    cpuRealtimePeriod: number;
    deviceCgroupRules: null;
    cpuRealtimeRuntime: number;
    iomaximumBandwidth: number;
    publishAllPorts: boolean;
    readonlyRootfs: boolean;
    binds: null;
    extraHosts: null;
    pidMode: string;
    volumesFrom: null;
    cgroupnsMode: string;
    ipcMode: string;
    dns: string[];
    dnsOptions: string[];
    portBindings?: {
        [key: string]: PortBinding[];
    };
    volumeDriver: string;
    capAdd: null;
    logConfig: DockerLogConfig;
    networkMode: string;
    autoRemove: boolean;
    capDrop: null;
    dnsSearch: string[];
    groupAdd: null;
    restartPolicy: DockerRestartPolicy;
    cgroup: string;
    containerIDFile: string;
    links: null;
    consoleSize: [number, number];
    oomScoreAdj: number;
    devices: null;
    securityOpt: null;
    deviceRequests: null;
    pidsLimit: null;
    cpuPercent: number;
    memory: number;
    readonlyPaths: string[];
    cgroupParent: string;
    memorySwap: number;
    blkioWeight: number;
    iomaximumIOps: number;
    oomKillDisable: boolean;
    nanoCpus: number;
    cpuCount: number;
    ulimits: null;
    maskedPaths: string[];
    cpusetCpus: string;
    shmSize: number;
    isolation: string;
    utsmode: string;
    usernsMode: string;
    cpusetMems: string;
};

type DockerGraphDriverData = {
    lowerDir: string;
    upperDir: string;
    mergedDir: string;
    workDir: string;
};

type DockerGraphDriver = {
    name: string;
    data: DockerGraphDriverData;
}

