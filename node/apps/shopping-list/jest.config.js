module.exports = {
  name: 'shopping-list',
  preset: '../../jest.config.js',
  coverageDirectory: '../../coverage/apps/shopping-list',
  snapshotSerializers: [
    'jest-preset-angular/build/AngularNoNgAttributesSnapshotSerializer.js',
    'jest-preset-angular/build/AngularSnapshotSerializer.js',
    'jest-preset-angular/build/HTMLCommentSerializer.js',
  ],
};
