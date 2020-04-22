module.exports = {
  name: 'mobile',
  preset: '../../jest.config.js',
  coverageDirectory: '../../coverage/apps/mobile',
  snapshotSerializers: [
    'jest-preset-angular/build/AngularNoNgAttributesSnapshotSerializer.js',
    'jest-preset-angular/build/AngularSnapshotSerializer.js',
    'jest-preset-angular/build/HTMLCommentSerializer.js',
  ],
};
