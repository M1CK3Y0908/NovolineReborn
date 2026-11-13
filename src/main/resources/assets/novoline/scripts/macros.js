Number.prototype.clamp = function(min, max) {
    return Math.min(Math.max(this, min), max);
};

var rgb = function(r, g, b) {
    return rgba(r, g, b, 255);
}

var rgba = function(r, g, b, a) {
    return ((a.clamp(0, 255) & 0xFF) << 24) |
        ((r.clamp(0, 255) & 0xFF) << 16) |
        ((g.clamp(0, 255) & 0xFF) << 8)  |
        ((b.clamp(0, 255) & 0xFF) << 0);
}

// --------------------------------------------------------------

var Timer = function() {
    this.lastMs = milliTime();

    this.hasPassed = function(duration, reset) {
        var hasPassed = milliTime() - this.lastMs > duration;
        if(hasPassed && reset) this.reset();
        return hasPassed;
    };

    this.reset = function() {
        this.lastMs = milliTime();
    };
};