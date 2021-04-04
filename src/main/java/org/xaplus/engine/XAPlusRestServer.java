package org.xaplus.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

/**
 * @since 1.0.0
 * @author Kirill Byvshev (k@byv.sh)
 */
public class XAPlusRestServer implements XAPlusFactory, XAPlusResource {
    static private final Logger logger = LoggerFactory.getLogger(XAPlusRestServer.class);

    private final String METHOD_PREPARE = "prepare";
    private final String METHOD_READIED = "readied";
    private final String METHOD_COMMIT = "commit";
    private final String METHOD_ROLLBACK = "rollback";
    private final String METHOD_FAILED = "failed";
    private final String METHOD_DONE = "done";
    private final String METHOD_RETRY = "retry";

    private final String hostname;
    private final int port;

    public XAPlusRestServer(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    @Override
    public XAPlusResource createXAPlusResource() {
        return this;
    }

    @Override
    public void start(Xid xid, int flags) throws XAException {
    }

    @Override
    public void end(Xid xid, int flags) throws XAException {
    }

    @Override
    public int prepare(Xid xid) throws XAException {
        return request(METHOD_PREPARE, "xid=" + xid.toString());
    }

    @Override
    public void readied(Xid xid) throws XAPlusException {
        try {
            request(METHOD_READIED, "xid=" + xid.toString());
        } catch (XAException xae) {
            if (xae.errorCode > 0) {
                throw new XAPlusException(xae.errorCode);
            } else {
                throw new XAPlusException(xae.getMessage());
            }
        }
    }

    @Override
    public void commit(Xid xid, boolean onePhase) throws XAException {
        request(METHOD_COMMIT, "xid=" + xid.toString());
    }

    @Override
    public void rollback(Xid xid) throws XAException {
        request(METHOD_ROLLBACK, "xid=" + xid.toString());
    }

    @Override
    public void failed(Xid xid) throws XAPlusException {
        try {
            request(METHOD_FAILED, "xid=" + xid.toString());
        } catch (XAException xae) {
            if (xae.errorCode > 0) {
                throw new XAPlusException(xae.errorCode);
            } else {
                throw new XAPlusException(xae.getMessage());
            }
        }
    }

    @Override
    public void done(Xid xid) throws XAPlusException {
        try {
            request(METHOD_DONE, "xid=" + xid.toString());
        } catch (XAException xae) {
            if (xae.errorCode > 0) {
                throw new XAPlusException(xae.errorCode);
            } else {
                throw new XAPlusException(xae.getMessage());
            }
        }
    }

    @Override
    public Xid[] recover(int flag) throws XAException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void forget(Xid xid) throws XAException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void retry(String serverId) throws XAPlusException {
        try {
            request(METHOD_RETRY, "serverId=" + serverId);
        } catch (XAException xae) {
            if (xae.errorCode > 0) {
                throw new XAPlusException(xae.errorCode);
            } else {
                throw new XAPlusException(xae.getMessage());
            }
        }
    }

    @Override
    public int getTransactionTimeout() throws XAException {
        return 0;
    }

    @Override
    public boolean setTransactionTimeout(int seconds) throws XAException {
        return false;
    }

    @Override
    public boolean isSameRM(XAResource xares) throws XAException {
        return false;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "=(hostname=" + hostname + ", port=" + port + ")";
    }

    private int request(String method, String parameters) throws XAException {
        if (method == null) {
            throw new NullPointerException("method is null");
        }
        if (parameters == null) {
            throw new NullPointerException("parameters is null");
        }
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "http://" + hostname + ":" + port + "/xaplus/" + method + "?" + parameters;
            if (logger.isTraceEnabled()) {
                logger.trace("REQUESTING: url={}", url);
            }
            if (!restTemplate.getForObject(url, Boolean.class)) {
                throw new XAException(XAException.XAER_RMERR);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("REQUESTED: url={}", url);
            }
            return XA_OK;
        } catch (RestClientException rce) {
            throw new XAException(rce.getMessage());
        }
    }
}
