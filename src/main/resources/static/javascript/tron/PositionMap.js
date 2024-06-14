class PositionMap {
    constructor() {
        this.map = new Map();
    }

    _hashKey(position) {
        return `${position[0]},${position[1]}`;
    }

    set(position, value) {
        const key = this._hashKey(position);
        this.map.set(key, value);
    }

    has(position) {
        const key = this._hashKey(position);
        return this.map.has(key);
    }

}