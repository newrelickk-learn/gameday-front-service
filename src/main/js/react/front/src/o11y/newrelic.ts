import { BrowserAgent } from '@newrelic/browser-agent/loaders/browser-agent'

const options = {
    init: {distributed_tracing:{enabled:true},privacy:{cookies_enabled:true}},
    info: {beacon:"bam.nr-data.net",errorBeacon:"bam.nr-data.net",licenseKey:"NRJS-d5fc04e71a6af75ae7d",applicationID:"1519106818",sa:1},
    loader_config: {accountID:"3873151",trustKey:"3830171",agentID:"1588910174",licenseKey:"NRJS-d5fc04e71a6af75ae7d",applicationID:"1519106818"},
}

const prodOptions = {
    init: {distributed_tracing:{enabled:true},privacy:{cookies_enabled:true}},
    info: {beacon:"bam.nr-data.net",errorBeacon:"bam.nr-data.net",licenseKey:"NRJS-d5fc04e71a6af75ae7d",applicationID:"1588910227",sa:1},
    loader_config: {accountID:"3873151",trustKey:"3830171",agentID:"1588910227",licenseKey:"NRJS-d5fc04e71a6af75ae7d",applicationID:"1588910227"},
}
export default new BrowserAgent(process.env.NODE_ENV === 'production' ? prodOptions : options)
