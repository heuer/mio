package com.semagia.mio.ctm;

import java.util.Map;

import com.semagia.mio.MIOException;
import com.semagia.mio.helpers.Literal;
import com.semagia.mio.helpers.SimpleMapHandler;

final class TemplateContext implements ITemplateContext {

    IParseContext _ctx;
    TemplateContext _parent;
    private IReference[] _focus;
    private int _focusIdx;
    private Map<IReference, IReference> _bindings;

    public TemplateContext(final IParseContext ctx, final TemplateContext tplCtx, final Map<IReference, IReference> bindings) {
        _ctx = ctx;
        _parent = tplCtx;
        _focus = new IReference[2];
        _focusIdx = -1;
        _bindings = bindings;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.ITemplateContext#getLiteral(com.semagia.mio.ctm.IReference)
     */
    @Override
    public Literal getLiteral(final IReference ref) throws MIOException {
        final IReference res = ref.isVariable() ? _getReferenceByVariable(ref) : ref;
        return res.isIRI() ? Literal.createIRI(res.getIRI()) : res.getLiteral();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.ITemplateContext#getLocator(java.lang.String)
     */
    @Override
    public String getIRI(final IReference var) throws MIOException {
        return _getReferenceByVariable(var).getIRI();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.ITemplateContext#getMapHandler()
     */
    @Override
    public SimpleMapHandler getMapHandler() {
        return _ctx.getMapHandler();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.ITemplateContext#getTopicReference(com.semagia.mio.ctm.IReference)
     */
    @Override
    public IReference getTopicReference(final IReference ref) throws MIOException {
        if (ref == Reference.TOPIC_IN_FOCUS) {
            return getFocus();
        }
        if (ref.isItemIdentifier() || ref.isIRI() || ref.isSubjectLocator()) {
            return ref;
        }
        if (ref.isVIID()) {
            return Reference.createIID(_getReferenceByVariable(ref).getIRI());
        }
        if (ref.isVSLO()) {
            return Reference.createSLO(_getReferenceByVariable(ref).getIRI());
        }
        if (ref.isVariable()) {
            IReference res = _getReferenceByVariable(ref);
            if (res.isWildcard() || res.isNamedWildcard()) {
                return getTopicReference(res);
            }
            return res;
        }
        if (ref.isWildcard() || ref.isNamedWildcard()) {
            IReference res = _bindings.get(ref);
            if (res == null) {
                res = _ctx.resolveWildcardIdentifier(_ctx.makeNextWildcardId(ref.isWildcard() ? null : (String)ref.getValue()));
                _bindings.put(ref, res);
            }
            return res;
        }
        throw new MIOException("Cannot find " + ref);
    }

    private IReference _getReferenceByVariable(IReference ref) throws MIOException {
        if (ref.isVIID() || ref.isVSLO()) {
            ref = Reference.createVariable(ref.getString());
        }
        if (!ref.isVariable()) {
            throw new MIOException("Internal error. Expected a variable, got: " + ref);
        }
        IReference res = _bindings.get(ref);
        if (res == null) {
            res = ref;
        }
        if (res.isVariable() && _parent != null) {
            res = _parent._getReferenceByVariable(res);
        }
        if (res == Reference.TOPIC_IN_FOCUS) {
            res = getFocus();
        }
        if (res == null) {
            throw new MIOException("Unknown variable " + ref.getString());
        }
        return res;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.ITemplateContext#pushFocus(com.semagia.mio.ctm.IReference)
     */
    @Override
    public void pushFocus(final IReference focus) {
        if (_focus.length == _focusIdx+1) {
            IReference[] topics = new IReference[_focus.length*2];
            System.arraycopy(_focus, 0, topics, 0, _focus.length);
            _focus = topics;
        }
        _focusIdx++;
        _focus[_focusIdx] = focus;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.ITemplateContext#popFocus()
     */
    @Override
    public void popFocus() {
        _focus[_focusIdx] = null;
        _focusIdx--;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.ITemplateContext#getFocus()
     */
    @Override
    public IReference getFocus() {
        if (_focusIdx < 0) {
            return _parent != null ? _parent.getFocus() : null;
        }
        return _focus[_focusIdx];
    }

    public void close() {
        _focus = null;
        _ctx = null;
        _parent = null;
        _bindings = null;
    }

    @Override
    public IParseContext getParseContext() {
        return _ctx;
    }
}
