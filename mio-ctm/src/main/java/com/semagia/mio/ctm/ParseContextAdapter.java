/**
 * 
 */
package com.semagia.mio.ctm;

import com.semagia.mio.MIOException;
import com.semagia.mio.ctm.api.IPrefixListener;

/**
 * @author lars
 *
 */
final class ParseContextAdapter extends AbstractParseContext {

    private final IReadOnlyTopicLookup _lookup;

    ParseContextAdapter(IReadOnlyTopicLookup lookup) {
        super();
        _lookup = lookup;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IReadOnlyTopicLookup#resolveQName(java.lang.String)
     */
    @Override
    public IReference resolveQName(final String qName) throws MIOException {
        return Reference.wrap(_lookup.resolveQName(qName));
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IReadOnlyTopicLookup#getTopicByWildcard(java.lang.String)
     */
    @Override
    public String makeNextWildcardId(final String name) {
        return _lookup.getTopicIdentifierByWildcard(name);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.ITopicLookup#registerPrefix(java.lang.String, java.lang.String)
     */
    @Override
    public void registerPrefix(String prefix, String iri) throws MIOException {
        throw new MIOException("Unsupported method");
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.ITopicLookup#setPrefixListener(com.semagia.mio.ctm.api.IPrefixListener)
     */
    @Override
    public void setPrefixListener(IPrefixListener listener) {
        // Ignore since prefixes are immutable.
    }

}
